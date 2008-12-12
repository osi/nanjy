package org.fotap.nanjy;

import org.fotap.nanjy.monitor.Sample;
import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
class Channels {
    private final Channel<VirtualMachineDescriptor> added = new MemoryChannel<VirtualMachineDescriptor>();
    private final Channel<VirtualMachineDescriptor> removed = new MemoryChannel<VirtualMachineDescriptor>();
    private final Channel<Sample> samples = new MemoryChannel<Sample>();

    Channel<VirtualMachineDescriptor> added() {
        return added;
    }

    Channel<VirtualMachineDescriptor> removed() {
        return removed;
    }

    Channel<Sample> samples() {
        return samples;
    }
}
