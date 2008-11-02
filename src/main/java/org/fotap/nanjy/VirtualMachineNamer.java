package org.fotap.nanjy;

import com.sun.tools.attach.VirtualMachine;

/**
 * Mechanism for providing a simple name for a virtual machine
 *
 * @author <a href="mailto:peter.royal@pobox.com">peter royal</a>
 */
public interface VirtualMachineNamer {
    String name( VirtualMachine vm );
}
