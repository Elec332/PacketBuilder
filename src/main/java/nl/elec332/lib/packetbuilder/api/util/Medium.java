package nl.elec332.lib.packetbuilder.api.util;

/**
 * Created by Elec332 on 2/28/2021
 */
public class Medium extends Number {

    public Medium() {
        error();
    }

    @Override
    public int intValue() {
        return error();
    }

    @Override
    public long longValue() {
        return error();
    }

    @Override
    public float floatValue() {
        return error();
    }

    @Override
    public double doubleValue() {
        return error();
    }

    private int error() {
        throw new UnsupportedOperationException(getClass() + " only exists as a marker for 24-bit numbers!");
    }

}
