package org.fotap.nanjy;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.fotap.nanjy.monitor.Monitor;
import org.fotap.nanjy.monitor.MonitorFactories;
import org.fotap.nanjy.monitor.MonitorFactory;
import org.fotap.nanjy.monitor.Sample;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.channels.Publisher;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class VirtualMachine implements Disposable {
    private static final Logger logger = LoggerFactory.getLogger( VirtualMachine.class );

    private final AddAndRemoveNotificationListener listener = new AddAndRemoveNotificationListener();
    private final Map<ObjectName, Disposable> monitors =
        Collections.synchronizedMap( new HashMap<ObjectName, Disposable>() );
    private final com.sun.tools.attach.VirtualMachine vm;
    private final JMXConnector connector;
    private final MBeanServerConnection connection;
    private final Channel<ObjectName> addedMBeans;
    private final Channel<ObjectName> removedMBeans;
    private final String name;
    private final MonitorFactories monitorFactories;
    private final Publisher<Sample> samples;
    private final Fiber sampleFiber;

    public VirtualMachine( VirtualMachineDescriptor descriptor,
                           AgentHolder agentHolder,
                           VirtualMachineNamer namer,
                           MonitorFactories monitorFactories,
                           Publisher<Sample> samples,
                           Fiber sampleFiber ) throws Exception
    {
        this.monitorFactories = monitorFactories;
        this.samples = samples;
        this.sampleFiber = sampleFiber;

        logger.debug( "attaching to {}", descriptor );

        vm = com.sun.tools.attach.VirtualMachine.attach( descriptor );

        String address = getConnectorAddress();

        if ( null == address ) {
            logger.debug( "loading agent into {}", descriptor );
            vm.loadAgent( agentHolder.getAgentPath() );

            address = getConnectorAddress();
        }

        JMXServiceURL url = new JMXServiceURL( address );

        logger.trace( "conencting to {} via {}", descriptor, address );

        connector = JMXConnectorFactory.connect( url );
        connection = connector.getMBeanServerConnection();

        if ( logger.isTraceEnabled() ) {
            logger.trace( "remote properties:\nagent: {}\nsystem {}",
                          vm.getAgentProperties(),
                          vm.getSystemProperties() );
        }

        // TODO name thread based on the VM name
        // TODO use java.lang.management.RuntimeMXBean
        name = namer.name( vm );
        addedMBeans = new MemoryChannel<ObjectName>();
        removedMBeans = new MemoryChannel<ObjectName>();
    }

    public void start( Fiber fiber, final Runnable callback ) {
        addedMBeans.subscribe( fiber, new Callback<ObjectName>() {
            @Override
            public void onMessage( ObjectName message ) {
                try {
                    MonitorFactory factory = monitorFactories.factoryFor( message );

                    if ( null == factory ) {
                        // TODO what's the proper log level here? we may care, may not.
                        logger.debug( "no monitor factory for {}", message );
                    } else {
                        final Monitor monitor = factory.create( name, message, connection, samples );
                        final org.jetlang.core.Disposable control =
                            sampleFiber.scheduleWithFixedDelay( monitor, 0, 10, TimeUnit.SECONDS );

                        logger.debug( "added {} / {}", message, name );

                        monitors.put( message, new Disposable() {
                            @Override
                            public void dispose() {
                                control.dispose();
                                monitor.dispose();
                            }
                        } );
                    }
                } catch( Exception e ) {
                    logger.error( "Unable to create monitor for " + message + " in " + name, e );
                }
            }
        } );
        removedMBeans.subscribe( fiber, new Callback<ObjectName>() {
            @Override
            public void onMessage( ObjectName message ) {
                logger.debug( "removed: {}", message );
            }
        } );

        fiber.execute( new Runnable() {
            @Override
            public void run() {
                try {
                    connection.addNotificationListener( MBeanServerDelegate.DELEGATE_NAME, listener, null, null );

                    for ( ObjectName name : connection.queryNames( null, null ) ) {
                        addedMBeans.publish( name );
                    }

                    callback.run();
                } catch( Exception e ) {
                    logger.error( "Unable to scan for MBeans in " + vm, e );
                }
            }
        } );
    }

    @Override
    public void dispose() {
        try {
            if ( null != listener ) {
                try {
                    connection.removeNotificationListener( MBeanServerDelegate.DELEGATE_NAME, listener );
                } catch( OperationsException e ) {
                    logger.trace( "Unable to cleanly remove listener", e );
                }
            }

            connector.close();
            vm.detach();
        } catch( IOException e ) {
            logger.debug( "Unable to cleanly dispose of " + vm, e );
        }
    }

    private String getConnectorAddress() throws IOException {
        // TODO a 'Broken pipe' here would mean that the VM went away..
        return vm.getAgentProperties().getProperty( "com.sun.management.jmxremote.localConnectorAddress" );
    }

    private class AddAndRemoveNotificationListener implements NotificationListener {
        @Override
        public void handleNotification( Notification notification, Object o ) {
            MBeanServerNotification serverNotification = (MBeanServerNotification) notification;

            if ( MBeanServerNotification.REGISTRATION_NOTIFICATION.equals( serverNotification.getType() ) ) {
                addedMBeans.publish( serverNotification.getMBeanName() );
            } else if ( MBeanServerNotification.UNREGISTRATION_NOTIFICATION.equals( serverNotification.getType() ) ) {
                removedMBeans.publish( serverNotification.getMBeanName() );
            }
        }
    }
}
