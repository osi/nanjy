package org.fotap.nanjy.monitor.platform

import java.lang.management.ClassLoadingMXBean
import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

/**
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public class ClassLoadingMXBeanMonitorFactory implements MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples) {
        ClassLoadingMXBean bean = ManagementFactory.newPlatformMXBeanProxy(connection, mbean.getCanonicalName(), ClassLoadingMXBean.class)
        return {
            run: {
                samples.publish new Sample("${name}/class-loading",
                                           System.currentTimeMillis(),
                                           bean.loadedClassCount,
                                           bean.unloadedClassCount,
                                           bean.totalLoadedClassCount)
            }

            dispose: {}
        } as Monitor;
    }
}