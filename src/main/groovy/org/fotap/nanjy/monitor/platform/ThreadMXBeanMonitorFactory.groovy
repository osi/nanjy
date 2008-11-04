package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.LongSample
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
        return {
            run: {
                def now = System.currentTimeMillis()
                samples.publish new LongSample(name + "/platform/thread/count", bean.threadCount, now)
                samples.publish new LongSample(name + "/platform/thread/daemon_count", bean.daemonThreadCount, now)
                samples.publish new LongSample(name + "/platform/thread/peak_count", bean.peakThreadCount, now)
                samples.publish new LongSample(name + "/platform/thread/total_started_count", bean.totalStartedThreadCount, now)
            }

            dispose: {}
        } as Monitor;
    }
}