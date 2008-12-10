package org.fotap.nanjy;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fotap.nanjy.monitor.GroovyScriptMonitorFactories;
import org.fotap.nanjy.monitor.Sample;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger( Main.class );

    public Main( URL monitorSource ) {
        Fiber core = new ThreadFiber( new RunnableExecutorImpl(), "core", false );

        core.start();

        Channel<VirtualMachineDescriptor> added = new MemoryChannel<VirtualMachineDescriptor>();
        Channel<VirtualMachineDescriptor> removed = new MemoryChannel<VirtualMachineDescriptor>();
        Channel<Sample> samples = new MemoryChannel<Sample>();
        Fiber sampling = new PoolFiberFactory( Executors.newFixedThreadPool( 1, new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger( 0 );

            @Override
            public Thread newThread( Runnable r ) {
                return new Thread( r, "sampler-" + counter.getAndIncrement() );
            }
        } ) ).create();

        sampling.start();

        new Connector( added,
                       removed,
                       core,
                       new VirtualMachineFactory( new AgentHolder(),
                                                  new MainClassOrJarFile(),
                                                  new GroovyScriptMonitorFactories( monitorSource ),
                                                  samples,
                                                  sampling )
        ).start();

        added.subscribe( core, new Callback<VirtualMachineDescriptor>() {
            @Override
            public void onMessage( VirtualMachineDescriptor message ) {
                logger.info( "added: {}", message );
            }
        } );

        removed.subscribe( core, new Callback<VirtualMachineDescriptor>() {
            @Override
            public void onMessage( VirtualMachineDescriptor message ) {
                logger.info( "removed: {}", message );
            }
        } );

        final String hostname = hostname();

        // TODO ouput on a separate thread
        samples.subscribe( core, new Callback<Sample>() {
            @Override
            public void onMessage( Sample sample ) {
                StringBuilder sb = new StringBuilder( 256 );
                sb.append( "PUTVAL " )
                    .append( hostname )
                    .append( "/jvm-" )
                    .append( sample.getName() )
                    .append( " " )
                    .append( TimeUnit.MILLISECONDS.toSeconds( sample.takenAt() ) );

                for ( Number number : sample.getValues() ) {
                    sb.append( ":" ).append( number );
                }

                System.out.println( sb );
            }
        } );

        Disposable scannerControl =
            core.scheduleWithFixedDelay( new Scanner( added, removed ), 0, 10, TimeUnit.SECONDS );
    }

    private static String hostname() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String name = address.getHostName();
            int dot = name.indexOf( '.' );

            return dot > 0 ? name.substring( 0, dot ) : name;
        } catch( UnknownHostException e ) {
            throw new RuntimeException( "Unable to determine host name", e );
        }
    }

    public static void main( String... args ) throws Exception {
        new Main( new File( args[0] ).toURI().toURL() );
    }
}
