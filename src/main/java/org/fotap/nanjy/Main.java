package org.fotap.nanjy;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.fotap.nanjy.monitor.GroovyScriptMonitorFactories;
import org.jetlang.fibers.Fiber;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Main {

    public Main( URL monitorSource ) {
        Fibers fibers = new Fibers();
        Channels channels = new Channels();

        Fiber core = fibers.core();

        new Connector( channels.added(),
                       channels.removed(),
                       core,
                       new VirtualMachineFactory( new AgentHolder(),
                                                  new MainClassOrJarFile(),
                                                  new GroovyScriptMonitorFactories( monitorSource ),
                                                  channels.samples(),
                                                  fibers.sampler() )
        ).start();

        new VirtualMachineLogger( core, channels.added(), channels.removed() );

        channels.samples().subscribe( fibers.emitter(), new CollectdEmitter() );

        core.scheduleWithFixedDelay( new Scanner( channels.added(), channels.removed() ), 0, 10, TimeUnit.SECONDS );
    }

    public static void main( String... args ) throws Exception {
        new Main( new File( args[0] ).toURI().toURL() );
    }
}
