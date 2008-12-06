import javax.management.MBeanServerConnection
import javax.management.ObjectName
import org.fotap.nanjy.monitor.Monitor
import org.fotap.nanjy.monitor.Sample
import org.jetlang.channels.Publisher

public class MonitorFactory implements org.fotap.nanjy.monitor.MonitorFactory {

    public Monitor create(String name, ObjectName mbean, MBeanServerConnection connection, Publisher<Sample> samples) {
        return null;
    }

}