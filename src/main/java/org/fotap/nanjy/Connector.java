package org.fotap.nanjy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetlang.channels.Subscriber;
import org.jetlang.core.Callback;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Connector {
    private static final Logger logger = LoggerFactory.getLogger( Connector.class );

    private final AgentHolder agentHolder = new AgentHolder();
    private final Map<VirtualMachineDescriptor, VirtualMachine> vms =
        Collections.synchronizedMap( new HashMap<VirtualMachineDescriptor, VirtualMachine>() );

    private final Subscriber<VirtualMachineDescriptor> added;
    private final Subscriber<VirtualMachineDescriptor> removed;
    private final ThreadFiber fiber;
    private final VirtualMachineNamer namer;
    private final MonitorFactoryRegistry monitorFactoryRegistry;

    public Connector( Subscriber<VirtualMachineDescriptor> added,
                      Subscriber<VirtualMachineDescriptor> removed,
                      VirtualMachineNamer namer, MonitorFactoryRegistry monitorFactoryRegistry )
    {
        this.added = added;
        this.removed = removed;
        this.namer = namer;
        this.monitorFactoryRegistry = monitorFactoryRegistry;
        this.fiber = new ThreadFiber( new RunnableExecutorImpl(), getClass().getName(), false );
    }

    public void start() {
        added.subscribe( fiber, new Added() );
        removed.subscribe( fiber, new Removed() );
        fiber.start();
    }

    private class Added implements Callback<VirtualMachineDescriptor> {
        @Override
        public void onMessage( final VirtualMachineDescriptor descriptor ) {
            try {
                final VirtualMachine machine =
                    new VirtualMachine( descriptor, agentHolder, namer, monitorFactoryRegistry );

                machine.start( new Runnable() {
                    @Override
                    public void run() {
                        vms.put( descriptor, machine );
                    }
                } );
            } catch( Exception e ) {
                logger.error( "Unable to connect to " + descriptor, e );
            }
        }
    }

    private class Removed implements Callback<VirtualMachineDescriptor> {
        @Override
        public void onMessage( VirtualMachineDescriptor descriptor ) {
            VirtualMachine machine = vms.get( descriptor );

            if ( null != machine ) {
                try {
                    machine.dispose();
                } catch( Exception e ) {
                    logger.error( "Unable to properly dispose of " + descriptor, e );
                }
            }
        }
    }
}
