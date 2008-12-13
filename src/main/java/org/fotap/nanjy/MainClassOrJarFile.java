package org.fotap.nanjy;

import com.sun.tools.attach.VirtualMachine;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class MainClassOrJarFile implements VirtualMachineNamer {
    @Override
    public String name( VirtualMachine vm ) throws Exception {
        String command = vm.getAgentProperties().getProperty( "sun.java.command" );
        int space = command.indexOf( ' ' );
        return space > 0 ? command.substring( 0, space ) : command;
    }
}
