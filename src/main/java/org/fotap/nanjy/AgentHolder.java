package org.fotap.nanjy;

import java.io.File;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
class AgentHolder {
    private final String path =
        System.getProperty( "java.home" ) + File.separator + "lib" + File.separator + "management-agent.jar";

    AgentHolder() {
        if ( !new File( path ).exists() ) {
            throw new IllegalStateException( "Unable to dynamically instrument virtual machines. " +
                                             "Management agent jar is not found. " +
                                             "Please ensure a Sun JDK is installed. Path: " + path );
        }
    }

    String getAgentPath() {
        return path;
    }
}
