package org.fotap.nanjy;

import com.sun.tools.attach.VirtualMachine;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class MainClassOrJarFile implements VirtualMachineNamer {
    @Override
    public String name( VirtualMachine vm ) {
        // TODO determine actual name
        return vm.toString();
    }
}
