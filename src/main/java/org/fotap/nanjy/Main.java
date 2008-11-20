package org.fotap.nanjy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.fotap.nanjy.monitor.Sample;
import org.fotap.nanjy.monitor.platform.PlatformMXBeans;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger( Main.class );

    public Main() {
        PoolFiberFactory fiberFactory = new PoolFiberFactory( Executors.newFixedThreadPool( 1 ) );

        Fiber core = fiberFactory.create();

        core.start();

        Channel<VirtualMachineDescriptor> added = new MemoryChannel<VirtualMachineDescriptor>();
        Channel<VirtualMachineDescriptor> removed = new MemoryChannel<VirtualMachineDescriptor>();
        Channel<Sample> samples = new MemoryChannel<Sample>();

        new Connector( added, removed, new MainClassOrJarFile(), new PlatformMXBeans(), samples ).start();

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

    public static void main( String... args ) {
        new Main();
    }
}
