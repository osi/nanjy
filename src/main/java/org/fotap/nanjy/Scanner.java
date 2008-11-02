package org.fotap.nanjy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetlang.channels.Publisher;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Scanner implements Runnable {
    private final Publisher<VirtualMachineDescriptor> added;
    private final Publisher<VirtualMachineDescriptor> removed;

    private Collection<VirtualMachineDescriptor> previousDescriptors = Collections.emptyList();

    public Scanner( Publisher<VirtualMachineDescriptor> added, Publisher<VirtualMachineDescriptor> removed ) {
        this.added = added;
        this.removed = removed;
    }

    @Override
    public void run() {
        List<VirtualMachineDescriptor> currentDescriptors = VirtualMachine.list();

        notify( currentDescriptors, previousDescriptors, added );
        notify( previousDescriptors, currentDescriptors, removed );

        previousDescriptors = currentDescriptors;
    }

    private void notify( Collection<VirtualMachineDescriptor> positive,
                         Collection<VirtualMachineDescriptor> negative,
                         Publisher<VirtualMachineDescriptor> publisher )
    {
        Set<VirtualMachineDescriptor> difference = new HashSet<VirtualMachineDescriptor>( positive );
        difference.removeAll( negative );

        for ( VirtualMachineDescriptor descriptor : difference ) {
            publisher.publish( descriptor );
        }
    }
}
