package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Descriptor
import static org.fotap.nanjy.monitor.Descriptor.gauge
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher



/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class ThreadMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples) {
        ThreadMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), ThreadMXBean.class)
        def started = new Descriptor("${name}/jvm/threads/started", gauge("total"))
        def counts = new Descriptor("${name}/jvm/threads/counts", gauge("total"), gauge("daemon"), gauge("peak"))

        return {
            run: {
                def now = System.currentTimeMillis()
                samples.publish new Sample(started, now, bean.totalStartedThreadCount)
                samples.publish new Sample(counts, now, bean.threadCount, bean.daemonThreadCount, bean.peakThreadCount)
            }

            dispose: {}
        } as Monitor;
    }
}