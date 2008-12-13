package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.MemoryUsage
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Descriptor
import static org.fotap.nanjy.monitor.Descriptor.counter
import static org.fotap.nanjy.monitor.Descriptor.gauge
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>                */
public class MemoryMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples)
    {
        MemoryMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), MemoryMXBean.class)
        Descriptor.Field[] fields = [gauge("init"), gauge("used"), gauge("committed"), gauge("max")]

        def descriptors = ["heap", "non_heap"].collect { new Descriptor("${name}/jvm/memory/${it}", fields) }
        def finalizationDescriptor = new Descriptor("${name}/jvm/objects_pending_finalization", counter("count"));

        return {
            run: {
                def now = System.currentTimeMillis()
                samples.publish new Sample(finalizationDescriptor, now, bean.objectPendingFinalizationCount)

                [bean.heapMemoryUsage, bean.nonHeapMemoryUsage].eachWithIndex {MemoryUsage usage, int index ->
                    samples.publish new Sample(descriptors[index], now, usage.init, usage.used, usage.committed, usage.max);
                }
            }

            dispose: {}
        } as Monitor
    }
}
