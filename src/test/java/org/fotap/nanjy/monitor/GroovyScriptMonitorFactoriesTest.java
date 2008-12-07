package org.fotap.nanjy.monitor;

import java.io.File;
import java.lang.management.ManagementFactory;
import javax.management.ObjectName;

import static org.junit.Assert.*;
import org.junit.*;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class GroovyScriptMonitorFactoriesTest {
    private GroovyScriptMonitorFactories monitorFactories;

    @Before
    public void setUp() throws Exception {
        monitorFactories = new GroovyScriptMonitorFactories( new File( "src/test/scripts" ).toURI().toURL() );
    }

    @Test
    public void loadsFactoryScriptBasedOffExactMBeanDomain() throws Exception {
        assertNotNull( monitorFactories.factoryFor( ObjectName.getInstance( "com.example.script", "type", "Foo" ) ) );
    }

    @Test
    public void loadsFactoryScriptBasedOffMBeanParentDomain() throws Exception {
        assertNotNull( monitorFactories.factoryFor( ObjectName.getInstance( "com.example.script.sub",
                                                                            "type",
                                                                            "Foo" ) ) );
    }

    @Test
    public void loadsFactoryClass() throws Exception {
        assertNotNull( monitorFactories.factoryFor( ObjectName.getInstance( "com.example.cls", "type", "Foo" ) ) );
    }

    @Test
    public void returnsNullWhenThereIsNoFactory() throws Exception {
        assertNull( monitorFactories.factoryFor( ObjectName.getInstance( "com.example.none", "type", "Foo" ) ) );
    }

    @Test
    public void platformMXBeansFactory() throws Exception {
        assertNotNull( monitorFactories.factoryFor( ObjectName.getInstance( ManagementFactory.MEMORY_MXBEAN_NAME ) ) );
    }

}
