package org.fotap.nanjy.monitor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class LongSample implements Sample {
    private final String name;
    private final long value;
    private final long takenAt;

    public LongSample( String name, long value, long takenAt ) {
        this.name = name;
        this.value = value;
        this.takenAt = takenAt;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long takenAt() {
        return takenAt;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        LongSample that = (LongSample) o;

        if ( takenAt != that.takenAt ) return false;
        if ( value != that.value ) return false;
        return name.equals( that.name );
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) ( value ^ ( value >>> 32 ) );
        result = 31 * result + (int) ( takenAt ^ ( takenAt >>> 32 ) );
        return result;
    }

    @Override
    public String toString() {
        return name + " := " + value + " @ " + takenAt;
    }
}
