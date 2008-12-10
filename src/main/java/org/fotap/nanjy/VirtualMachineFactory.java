package org.fotap.nanjy;

import org.fotap.nanjy.monitor.MonitorFactories;
import org.fotap.nanjy.monitor.Sample;
import org.jetlang.channels.Publisher;
import org.jetlang.fibers.Fiber;

import com.sun.tools.attach.VirtualMachineDescriptor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class VirtualMachineFactory {
    private final AgentHolder agentHolder;
    private final VirtualMachineNamer namer;
    private final MonitorFactories monitorFactories;
    private final Publisher<Sample> samples;
    private final Fiber sampleFiber;

    public VirtualMachineFactory( AgentHolder agentHolder,
                                  VirtualMachineNamer namer,
                                  MonitorFactories monitorFactories,
                                  Publisher<Sample> samples, Fiber sampleFiber )
    {
        this.agentHolder = agentHolder;
        this.namer = namer;
        this.monitorFactories = monitorFactories;
        this.samples = samples;
        this.sampleFiber = sampleFiber;
    }

    public VirtualMachine create( VirtualMachineDescriptor descriptor ) throws Exception {
        return new VirtualMachine( descriptor, agentHolder, namer, monitorFactories, samples, sampleFiber );
    }
}
