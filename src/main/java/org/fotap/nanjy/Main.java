package org.fotap.nanjy;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

import org.fotap.nanjy.monitor.GroovyScriptMonitorFactories;
import org.jetlang.fibers.Fiber;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Main {

    public Main( Options options ) throws MalformedURLException {
        Fibers fibers = new Fibers();
        Channels channels = new Channels();

        Fiber core = fibers.core();

        new Connector( channels.added(),
                       channels.removed(),
                       core,
                       new VirtualMachineFactory(
                           new AgentHolder(),
                           new MainClassOrJarFile(),
                           new GroovyScriptMonitorFactories( options.monitorScriptsSource.toURI().toURL() ),
                           channels.samples(),
                           fibers.sampler() )
        ).start();

        new VirtualMachineLogger( core, channels.added(), channels.removed() );

        channels.samples().subscribe( fibers.emitter(), new CollectdEmitter() );

        core.scheduleWithFixedDelay( new Scanner( channels.added(), channels.removed() ),
                                     0,
                                     options.virtualMachineScanInterval,
                                     TimeUnit.SECONDS );
    }

    @SuppressWarnings( { "UseOfSystemOutOrSystemErr" } )
    public static void main( String... args ) throws Exception {
        Options options = new Options();
        CmdLineParser parser = new CmdLineParser( options );
        parser.setUsageWidth( 120 );

        try {
            parser.parseArgument( args );
        } catch( CmdLineException e ) {
            System.err.println( e.getMessage() );
            System.err.println( "nanjy [options]" );
            parser.printUsage( System.err );

            System.exit( -1 );
        }

        new Main( options );
    }
}
