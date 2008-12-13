package org.fotap.nanjy.monitor.platform

import java.lang.management.ClassLoadingMXBean
import java.lang.management.ManagementFactory
import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Descriptor
import static org.fotap.nanjy.monitor.Descriptor.counter
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
        def descriptor = new Descriptor("${name}/jvm/class_loading", counter("Loaded"), counter("Unloaded"), counter("Total"))

        return {
            run: {
                samples.publish new Sample(descriptor,
                                           System.currentTimeMillis(),
                                           bean.loadedClassCount,
                                           bean.unloadedClassCount,
                                           bean.totalLoadedClassCount)
            }

            dispose: {}
        } as Monitor;
    }
}