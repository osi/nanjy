package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.MemoryMXBean
import java.lang.management.MemoryUsage
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher


/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>           */
public class MemoryMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples)
    {
        MemoryMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), MemoryMXBean.class)

        return {
            run: {
                def now = System.currentTimeMillis()
                samples.publish new Sample("${name}/gauge-objects_pending_finalization", now, bean.objectPendingFinalizationCount)

                ["heap": bean.heapMemoryUsage, "non_heap": bean.nonHeapMemoryUsage].each {
                    String type = it.key
                    MemoryUsage usage = it.value

                    samples.publish new Sample("${name}/memory/${type}", now, usage.init, usage.used, usage.committed, usage.max);
                }
            }

            dispose: {}
        } as Monitor
    }
}
