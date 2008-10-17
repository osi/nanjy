package org.fotap.nanjy;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Main {

    public Main() {
        PoolFiberFactory fiberFactory = new PoolFiberFactory( Executors.newCachedThreadPool() );

        Fiber core = fiberFactory.create();

        core.start();

        Channel<VirtualMachineDescriptor> added = new MemoryChannel<VirtualMachineDescriptor>();
        Channel<VirtualMachineDescriptor> removed = new MemoryChannel<VirtualMachineDescriptor>();

        Disposable scannerControl =
            core.scheduleWithFixedDelay( new Scanner( added, removed ), 0, 10, TimeUnit.SECONDS );

        added.subscribe( core, new Callback<VirtualMachineDescriptor>() {
            public void onMessage( VirtualMachineDescriptor message ) {
                System.out.println( "added: " + message );
            }
        } );

        removed.subscribe( core, new Callback<VirtualMachineDescriptor>() {
            public void onMessage( VirtualMachineDescriptor message ) {
                System.out.println( "removed: " + message );
            }
        } );
    }

    public static void main( String... args ) {
        new Main();
    }
}
