package org.fotap.nanjy.monitor;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.jetlang.channels.Publisher;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public interface MonitorFactory {
    Monitor create( String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples )
        throws Exception;
}
