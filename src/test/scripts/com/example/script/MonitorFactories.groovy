import javax.management.ObjectName
import org.fotap.nanjy.monitor.MonitorFactory

public class MonitorFactories implements org.fotap.nanjy.monitor.MonitorFactories {

    public MonitorFactory factoryFor(ObjectName mbean) {
        return {

        } as MonitorFactory;
    }

}