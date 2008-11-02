package org.fotap.nanjy;

import javax.management.ObjectName;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class PlatformMXBeans implements MonitorFactoryRegistry {
    @Override
    public MonitorFactory factoryFor( ObjectName mbean ) {
        // TODO return factories based off of java.lang.management.ManagementFactory
        return null;
    }
}
