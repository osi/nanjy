package com.example.cls;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.fotap.nanjy.monitor.Monitor;
import org.fotap.nanjy.monitor.Sample;
import org.jetlang.channels.Publisher;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class MonitorFactory implements org.fotap.nanjy.monitor.MonitorFactory {
    @Override
    public Monitor create( String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples )
        throws Exception
    {
        throw new UnsupportedOperationException();
    }
}
