package org.fotap.nanjy.monitor;

import javax.management.ObjectName;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public interface MonitorFactories {
    MonitorFactory factoryFor( ObjectName mbean );
}
