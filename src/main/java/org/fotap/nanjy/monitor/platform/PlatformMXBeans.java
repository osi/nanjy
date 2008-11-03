package org.fotap.nanjy.monitor.platform;

import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

import org.fotap.nanjy.monitor.MonitorFactory;
import org.fotap.nanjy.monitor.MonitorFactoryRegistry;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class PlatformMXBeans implements MonitorFactoryRegistry {
    @Override
    public MonitorFactory factoryFor( ObjectName mbean ) {
        if ( ManagementFactory.MEMORY_MXBEAN_NAME.equals( mbean.getCanonicalName() ) ) {
            return new MemoryMXBeanMonitorFactory();
        }

        // TODO return factories based off of java.lang.management.ManagementFactory
        return null;
    }
}
