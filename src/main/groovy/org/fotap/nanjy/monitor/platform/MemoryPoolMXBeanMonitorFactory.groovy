package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
import java.lang.management.MemoryUsage
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>          */
public class MemoryPoolMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples)
    {
        MemoryPoolMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), MemoryPoolMXBean.class)
        def prefix = "${name}/memory-pool/${bean.name}/"

        return {
            run: {
                def now = System.currentTimeMillis()

                ["current": bean.usage, "peak": bean.peakUsage, "after-last-collection": bean.collectionUsage].each {
                    String type = it.key
                    MemoryUsage usage = it.value

                    if ( null != usage ) {
                        samples.publish new Sample(prefix + type, now, usage.init, usage.used, usage.committed, usage.max);
                    }
                }
            }

            dispose: {}
        } as Monitor
    }
}
