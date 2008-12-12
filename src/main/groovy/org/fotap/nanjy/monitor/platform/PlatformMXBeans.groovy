package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import javax.management.ObjectName
import org.fotap.nanjy.monitor.MonitorFactories
import org.fotap.nanjy.monitor.MonitorFactory

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>           */
public class PlatformMXBeans implements MonitorFactories {

    public MonitorFactory factoryFor(ObjectName mbean) {
        switch( mbean.toString() ) {
            case ManagementFactory.MEMORY_MXBEAN_NAME:
                return new MemoryMXBeanMonitorFactory();
            case ManagementFactory.THREAD_MXBEAN_NAME:
                return new ThreadMXBeanMonitorFactory();
            case ManagementFactory.CLASS_LOADING_MXBEAN_NAME:
                return new ClassLoadingMXBeanMonitorFactory();
            case { it.startsWith(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE) }:
                return new MemoryPoolMXBeanMonitorFactory();
            case { it.startsWith(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE) }:
                return new GarbageCollectorMXBeanMonitorFactory();
        }

        return null;
    }
}
