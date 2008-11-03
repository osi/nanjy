package org.fotap.nanjy.monitor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class DoubleSample implements Sample {
    private final String name;
    private final double value;
    private final long takenAt;

    public DoubleSample( String name, double value, long takenAt ) {
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

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DoubleSample that = (DoubleSample) o;

        if ( takenAt != that.takenAt ) return false;
        if ( Double.compare( that.value, value ) != 0 ) return false;
        return name.equals( that.name );
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        temp = value != +0.0d ? Double.doubleToLongBits( value ) : 0L;
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        result = 31 * result + (int) ( takenAt ^ ( takenAt >>> 32 ) );
        return result;
    }

    @Override
    public String toString() {
        return name + " := " + value + " @ " + takenAt;
    }
}