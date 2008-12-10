package org.fotap.nanjy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetlang.channels.Subscriber;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Connector {
    private static final Logger logger = LoggerFactory.getLogger( Connector.class );

    private final Map<VirtualMachineDescriptor, VirtualMachine> vms =
        Collections.synchronizedMap( new HashMap<VirtualMachineDescriptor, VirtualMachine>() );

    private final Subscriber<VirtualMachineDescriptor> added;
    private final Subscriber<VirtualMachineDescriptor> removed;
    private final Fiber fiber;
    private final VirtualMachineFactory virtualMachineFactory;

    public Connector( Subscriber<VirtualMachineDescriptor> added,
                      Subscriber<VirtualMachineDescriptor> removed,
                      Fiber fiber,
                      VirtualMachineFactory virtualMachineFactory )
    {
        this.added = added;
        this.removed = removed;
        this.fiber = fiber;
        this.virtualMachineFactory = virtualMachineFactory;
    }

    public void start() {
        added.subscribe( fiber, new Added() );
        removed.subscribe( fiber, new Removed() );
    }

    private class Added implements Callback<VirtualMachineDescriptor> {
        @Override
        public void onMessage( final VirtualMachineDescriptor descriptor ) {
            try {
                final VirtualMachine machine = virtualMachineFactory.create( descriptor );

                machine.start( fiber, new Runnable() {
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
