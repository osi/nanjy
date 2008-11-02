package org.fotap.nanjy;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public interface MonitorFactory {
    Monitor create( ObjectName mbean, MBeanServerConnection connection );
}
