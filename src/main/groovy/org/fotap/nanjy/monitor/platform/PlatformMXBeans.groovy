package org.fotap.nanjy.monitor.platform

import java.lang.management.ManagementFactory
import javax.management.ObjectName
import org.fotap.nanjy.monitor.MonitorFactory
import org.fotap.nanjy.monitor.MonitorFactoryRegistry

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a>     */
public class PlatformMXBeans implements MonitorFactoryRegistry {

    public MonitorFactory factoryFor(ObjectName mbean) {
        switch( mbean.getCanonicalName() ) {
            case ManagementFactory.MEMORY_MXBEAN_NAME:
                return new MemoryMXBeanMonitorFactory();
            case ManagementFactory.THREAD_MXBEAN_NAME:
                return new ThreadMXBeanMonitorFactory();
        }

        // TODO return factories based off of java.lang.management.ManagementFactory
        return null;
    }
}
