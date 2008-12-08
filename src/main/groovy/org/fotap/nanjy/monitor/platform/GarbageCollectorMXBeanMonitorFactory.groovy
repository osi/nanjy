package org.fotap.nanjy.monitor.platform

import java.lang.management.GarbageCollectorMXBean
import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>            */
public class GarbageCollectorMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples)
    {
        GarbageCollectorMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), GarbageCollectorMXBean.class)
        def prefix = "jvm/${name}/gc/${bean.name}"

        return {
            run: {
                def now = System.currentTimeMillis()

                samples.publish new Sample(prefix, now, bean.collectionCount, bean.collectionTime)
            }

            dispose: {}
        } as Monitor
    }
}
