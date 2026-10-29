/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.collections;

import java.util.HashMap;

public class MultiValueSet<T>
extends HashMap<T, Object> {
    private static final long serialVersionUID = 8071544899414292397L;

    public MultiValueSet() {
    }

    public MultiValueSet(int n) {
        super(n);
    }

    public MultiValueSet(MultiValueSet<T> multiValueSet) {
        super(multiValueSet);
    }

    public void set(T t, Object object) {
        this.put(t, object);
    }

    public void set(T t, String string) {
        this.put(t, string);
    }

    public void set(T t, boolean bl) {
        this.put(t, bl ? Boolean.TRUE : Boolean.FALSE);
    }

    public void set(T t, int n) {
        this.put(t, n);
    }

    public void set(T t, int[] nArray) {
        this.put(t, nArray);
    }

    public void set(T t, long l) {
        this.put(t, l);
    }

    public void set(T t, double d) {
        this.put(t, d);
    }

    public void set(T t, Enum<?> enum_) {
        this.put(t, enum_);
    }

    public void unset(T t) {
        this.remove(t);
    }

    public boolean isSet(T t) {
        return this.get(t) != null;
    }

    @Override
    public MultiValueSet<T> clone() {
        return new MultiValueSet<T>(this);
    }

    public boolean getBool(T t) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).intValue() != 0;
        }
        if (v instanceof String) {
            return Boolean.parseBoolean((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v;
        }
        throw new IllegalArgumentException("Boolean value required, but found: " + v + "!");
    }

    public boolean getBool(T t, boolean bl) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).intValue() != 0;
        }
        if (v instanceof String) {
            return Boolean.parseBoolean((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v;
        }
        return bl;
    }

    public int getInteger(T t) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).intValue();
        }
        if (v instanceof String) {
            return Integer.parseInt((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v != false ? 1 : 0;
        }
        throw new IllegalArgumentException("Integer value required, but found: " + v + "!");
    }

    public int getInteger(T t, int n) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).intValue();
        }
        if (v instanceof String) {
            return Integer.parseInt((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v != false ? 1 : 0;
        }
        return n;
    }

    public int[] getIntegerArray(T t) {
        Object v = this.get(t);
        if (v instanceof int[]) {
            return (int[])v;
        }
        if (v instanceof Number) {
            return new int[]{((Number)v).intValue()};
        }
        if (v instanceof String) {
            String[] stringArray = ((String)v).split(";");
            int[] nArray = new int[stringArray.length];
            int n = 0;
            for (String string : stringArray) {
                nArray[n++] = Integer.parseInt(string);
            }
            return nArray;
        }
        throw new IllegalArgumentException("Integer array required, but found: " + v + "!");
    }

    public int[] getIntegerArray(T t, int[] nArray) {
        try {
            return this.getIntegerArray(t);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return nArray;
        }
    }

    public long getLong(T t) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).longValue();
        }
        if (v instanceof String) {
            return Long.parseLong((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v != false ? 1L : 0L;
        }
        throw new IllegalArgumentException("Long value required, but found: " + v + "!");
    }

    public long getLong(T t, long l) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).longValue();
        }
        if (v instanceof String) {
            return Long.parseLong((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v != false ? 1L : 0L;
        }
        return l;
    }

    public double getDouble(T t) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).doubleValue();
        }
        if (v instanceof String) {
            return Double.parseDouble((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v != false ? 1.0 : 0.0;
        }
        throw new IllegalArgumentException("Double value required, but found: " + v + "!");
    }

    public double getDouble(T t, double d) {
        Object v = this.get(t);
        if (v instanceof Number) {
            return ((Number)v).doubleValue();
        }
        if (v instanceof String) {
            return Double.parseDouble((String)v);
        }
        if (v instanceof Boolean) {
            return (Boolean)v != false ? 1.0 : 0.0;
        }
        return d;
    }

    public String getString(T t) {
        Object v = this.get(t);
        if (v != null) {
            return String.valueOf(v);
        }
        throw new IllegalArgumentException("String value required, but not specified!");
    }

    public String getString(T t, String string) {
        Object v = this.get(t);
        if (v != null) {
            return String.valueOf(v);
        }
        return string;
    }

    public Object getObject(T t) {
        return this.get(t);
    }

    public Object getObject(T t, Object object) {
        Object v = this.get(t);
        if (v != null) {
            return v;
        }
        return object;
    }

    public <E extends Enum<E>> E getEnum(T t, Class<E> clazz) {
        Object v = this.get(t);
        if (v != null && clazz.isInstance(v)) {
            return (E)((Enum)v);
        }
        if (v instanceof String) {
            return Enum.valueOf(clazz, (String)v);
        }
        throw new IllegalArgumentException("Enum value of type " + clazz.getName() + "required, but found: " + v + "!");
    }

    public <E extends Enum<E>> E getEnum(T t, Class<E> clazz, E e) {
        Object v = this.get(t);
        if (v != null && clazz.isInstance(v)) {
            return (E)((Enum)v);
        }
        if (v instanceof String) {
            return Enum.valueOf(clazz, (String)v);
        }
        return e;
    }
}

