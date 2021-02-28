package nl.elec332.lib.packetbuilder.util;

/**
 * Created by Elec332 on 2/28/2021
 */
public class NumberHelper {

    public static byte toByte(Object o) {
        return toNumber(o).byteValue();
    }

    public static short toShort(Object o) {
        return toNumber(o).shortValue();
    }

    public static int toInt(Object o) {
        return toNumber(o).intValue();
    }

    public static long toLong(Object o) {
        return toNumber(o).longValue();
    }

    public static float toFloat(Object o) {
        return toNumber(o).floatValue();
    }

    public static double toDouble(Object o) {
        return toNumber(o).doubleValue();
    }

    public static Number toNumber(Object o) {
        return (Number) o;
    }

    public static boolean equals(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null) {
            return false;
        }
        if (a.equals(b)) {
            return true;
        }
        if (a instanceof Number && b instanceof Number) {
            return equals((Number) a, (Number) b);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> Class<N> unWrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return (Class<N>) type;
        }
        if (byte.class == type) {
            return (Class<N>) Byte.class;
        }
        if (short.class == type) {
            return (Class<N>) Short.class;
        }
        if (int.class == type) {
            return (Class<N>) Integer.class;
        }
        if (long.class == type) {
            return (Class<N>) Long.class;
        }
        if (float.class == type) {
            return (Class<N>) Float.class;
        }
        if (double.class == type) {
            return (Class<N>) Double.class;
        }
        throw new UnsupportedOperationException("" + type);
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N cast(Number from, Class<N> to) {
        if (to == Number.class) {
            return (N) from;
        }
        to = unWrap(to);
        if (Byte.class == to) {
            return (N) (Byte) from.byteValue();
        }
        if (Short.class == to) {
            return (N) (Short) from.shortValue();
        }
        if (Integer.class == to) {
            return (N) (Integer) from.intValue();
        }
        if (Long.class == to) {
            return (N) (Long) from.longValue();
        }
        if (Float.class == to) {
            return (N) (Float) from.floatValue();
        }
        if (Double.class == to) {
            return (N) (Double) from.doubleValue();
        }
        throw new UnsupportedOperationException("" + to);
    }

    public static boolean equals(Number a, Number b) {
        Class<?> at = a.getClass();
        if (Byte.class == at) {
            return a.byteValue() == b.byteValue();
        }
        if (Short.class == at) {
            return a.shortValue() == b.shortValue();
        }
        if (Integer.class == at) {
            return a.intValue() == b.intValue();
        }
        if (Long.class == at) {
            return a.longValue() == b.longValue();
        }
        if (Float.class == at) {
            return a.floatValue() == b.floatValue();
        }
        if (Double.class == at) {
            return a.doubleValue() == b.doubleValue();
        }
        throw new UnsupportedOperationException();
    }

}
