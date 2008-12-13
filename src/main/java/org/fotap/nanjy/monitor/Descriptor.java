package org.fotap.nanjy.monitor;

/** @author <a href="mailto:peter.royal@pobox.com">peter royal</a> */
public class Descriptor {
    public enum Type {
        Gauge,
        Counter
    }

    public static class Field {
        private final Type type;
        private final String description;

        public Field( Type type, String description ) {
            this.type = type;
            this.description = description;
        }

        public Type getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String name;
    private final Field[] fields;

    public Descriptor( String name, Field... fields ) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public Field[] getFields() {
        return fields;
    }

    public static Field counter( String description ) {
        return new Field( Type.Counter, description );
    }

    public static Field gauge( String description ) {
        return new Field( Type.Gauge, description );
    }
}
