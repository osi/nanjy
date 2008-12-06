package com.example.override;

import javax.management.ObjectName;

import org.fotap.nanjy.monitor.MonitorFactory;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class MonitorFactories implements org.fotap.nanjy.monitor.MonitorFactories {
    @Override
    public MonitorFactory factoryFor( ObjectName mbean ) {
        throw new UnsupportedOperationException( "should have picked the groovy script" );
    }
}