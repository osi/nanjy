package org.fotap.nanjy.monitor;

import java.util.Arrays;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Sample {
    private final Descriptor descriptor;
    private final long takenAt;
    private final Number[] values;

    public Sample( Descriptor descriptor, long takenAt, Number... values ) {
        this.takenAt = takenAt;
        this.descriptor = descriptor;
        this.values = values;
    }

    public Descriptor getDescriptor() {
        return descriptor;
    }

    public long takenAt() {
        return takenAt;
    }

    public Number[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return descriptor.getName() + " := " + Arrays.toString( values ) + " @ " + takenAt;
    }
}
