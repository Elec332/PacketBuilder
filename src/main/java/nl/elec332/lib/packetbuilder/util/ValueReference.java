package nl.elec332.lib.packetbuilder.util;

/**
 * Created by Elec332 on 2/28/2021
 */
public class ValueReference<T> implements nl.elec332.lib.packetbuilder.api.util.ValueReference<T> {

    public ValueReference() {
        this(null);
    }

    public ValueReference(T value) {
        accept(value);
    }

    private T value;

    @Override
    public T get() {
        return value;
    }

    @Override
    public void accept(T t) {
        this.value = t;
    }

}
