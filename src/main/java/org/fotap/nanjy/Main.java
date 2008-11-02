package org.fotap.nanjy;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        Disposable scannerControl =
            core.scheduleWithFixedDelay( new Scanner( added, removed ), 0, 10, TimeUnit.SECONDS );

        new Connector( added, removed, new MainClassOrJarFile(), new PlatformMXBeans() ).start();

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
    }

    public static void main( String... args ) {
        new Main();
    }
}
