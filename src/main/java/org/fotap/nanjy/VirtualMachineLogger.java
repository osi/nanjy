package org.fotap.nanjy;

import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.attach.VirtualMachineDescriptor;

public class VirtualMachineLogger {
    private static final Logger logger = LoggerFactory.getLogger( VirtualMachineLogger.class );

    public VirtualMachineLogger( Fiber fiber,
                                 Channel<VirtualMachineDescriptor> added,
                                 Channel<VirtualMachineDescriptor> removed )
    {
        added.subscribe( fiber, new Callback<VirtualMachineDescriptor>() {
            @Override
            public void onMessage( VirtualMachineDescriptor message ) {
                logger.info( "added: {}", message );
            }
        } );

        removed.subscribe( fiber, new Callback<VirtualMachineDescriptor>() {
            @Override
            public void onMessage( VirtualMachineDescriptor message ) {
                logger.info( "removed: {}", message );
            }
        } );
    }
}