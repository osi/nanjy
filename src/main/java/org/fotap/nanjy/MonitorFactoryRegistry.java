package org.fotap.nanjy;

import javax.management.ObjectName;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public interface MonitorFactoryRegistry {
    MonitorFactory factoryFor( ObjectName mbean );
}
