package org.fotap.nanjy.monitor;

import java.util.Arrays;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Sample {
    private final String name;
    private final long takenAt;
    private final Number[] values;

    public Sample( String name, long takenAt, Number... values ) {
        this.name = name;
        this.takenAt = takenAt;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public long takenAt() {
        return takenAt;
    }

    public Number[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        return name + " := " + Arrays.toString( values ) + " @ " + takenAt;
    }
}
