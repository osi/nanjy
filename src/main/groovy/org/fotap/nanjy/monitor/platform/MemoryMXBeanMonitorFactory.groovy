package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.MemoryUsage
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.LongSample
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>         */
public class MemoryMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples)
    {
        MemoryMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), MemoryMXBean.class)

        return {
            run: {
                def now = System.currentTimeMillis()
                samples.publish new LongSample(name + "/platform/memory/objects_pending_finalization", bean.objectPendingFinalizationCount, now)

                ["heap": bean.heapMemoryUsage, "non_heap": bean.nonHeapMemoryUsage].each {
                    String type = it.key
                    MemoryUsage usage = it.value

                    if ( usage.init > -1 ) {
                        samples.publish new LongSample(name + "/platform/memory/" + type + "/init", usage.init, now);
                    }

                    if ( usage.max > -1 ) {
                        samples.publish new LongSample(name + "/platform/memory/" + type + "/max", usage.max, now);
                    }

                    samples.publish new LongSample(name + "/platform/memory/" + type + "/used", usage.used, now);
                    samples.publish new LongSample(name + "/platform/memory/" + type + "/committed", usage.committed, now);
                }
            }

            dispose: {}
        } as Monitor
    }
}
