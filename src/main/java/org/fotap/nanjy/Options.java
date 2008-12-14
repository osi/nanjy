package org.fotap.nanjy;

import java.io.File;

import org.kohsuke.args4j.Option;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Options {
    @Option( name = "--interval",
             usage = "Interval between scans for new virtual machines (in seconds)",
             metaVar = "SECONDS" )
    public int virtualMachineScanInterval = 10;

    @Option( name = "-m",
             aliases = "--monitors",
             usage = "Location of additional monitors implemented in Groovy",
             metaVar = "PATH" )
    public File monitorScriptsSource = new File( "monitors" );
}
