package org.fotap.nanjy.monitor.platform;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.fotap.nanjy.monitor.LongSample;
import org.fotap.nanjy.monitor.Monitor;
import org.fotap.nanjy.monitor.MonitorFactory;
import org.fotap.nanjy.monitor.Sample;
import org.jetlang.channels.Publisher;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class MemoryMXBeanMonitorFactory implements MonitorFactory {
    @Override
    public Monitor create( String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples )
        throws Exception
    {

        return new MemoryMXBeanMonitor( name,
                                        ManagementFactory.newPlatformMXBeanProxy( connection,
                                                                                  mbean.getCanonicalName(),
                                                                                  MemoryMXBean.class ),
                                        samples );
    }

    private static class MemoryMXBeanMonitor implements Monitor {
        private final String name;
        private final MemoryMXBean bean;
        private final Publisher<Sample> samples;

        public MemoryMXBeanMonitor( String name, MemoryMXBean bean, Publisher<Sample> samples ) {
            this.name = name;
            this.bean = bean;
            this.samples = samples;
        }

        @Override
        public void run() {
            long takenAt = System.currentTimeMillis();

            samples.publish( new LongSample( name + "/platform/memory/objects_pending_finalization",
                                             bean.getObjectPendingFinalizationCount(),
                                             takenAt ) );

            sampleMemoryUsage( bean.getHeapMemoryUsage(), "heap", takenAt );
            sampleMemoryUsage( bean.getNonHeapMemoryUsage(), "non_heap", takenAt );
        }

        private void sampleMemoryUsage( MemoryUsage usage, String type, long takenAt ) {
            if ( usage.getInit() > -1 ) {
                samples.publish( new LongSample( name + "/platform/memory/" +
                                                 type +
                                                 "/init",
                                                 usage.getInit(),
                                                 takenAt ) );
            }
            if ( usage.getMax() > -1 ) {
                samples.publish( new LongSample( name + "/platform/memory/" +
                                                 type +
                                                 "/max", usage.getMax(), takenAt ) );
            }

            samples.publish( new LongSample( name + "/platform/memory/" +
                                             type +
                                             "/used", usage.getUsed(), takenAt ) );
            samples.publish( new LongSample( name + "/platform/memory/" +
                                             type +
                                             "/committed",
                                             usage.getCommitted(),
                                             takenAt ) );
        }

        @Override
        public void dispose() {
        }
    }
}
