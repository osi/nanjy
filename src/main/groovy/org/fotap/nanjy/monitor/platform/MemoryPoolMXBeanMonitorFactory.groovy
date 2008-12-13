package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
import java.lang.management.MemoryUsage
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Descriptor
import static org.fotap.nanjy.monitor.Descriptor.gauge
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>                */
public class MemoryPoolMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples)
    {
        MemoryPoolMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), MemoryPoolMXBean.class)

        Descriptor.Field[] fields = [gauge("init"), gauge("used"), gauge("committed"), gauge("max")]
        def descriptors = ["current", "peak", "after-last-collection"].collect { new Descriptor("${name}/jvm/memory-pool/${bean.name}/${it}", fields) }

        return {
            run: {
                def now = System.currentTimeMillis()

                [bean.usage, bean.peakUsage, bean.collectionUsage].eachWithIndex {MemoryUsage usage, int index ->
                    if ( null != usage ) {
                        samples.publish new Sample(descriptors[index], now, usage.init, usage.used, usage.committed, usage.max);
                    }
                }
            }

            dispose: {}
        } as Monitor
    }
}
