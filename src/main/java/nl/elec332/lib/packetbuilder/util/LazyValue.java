package nl.elec332.lib.packetbuilder.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 3/1/2021
 */
public class LazyValue<T> implements Supplier<T> {

    public LazyValue(Supplier<T> factory, Consumer<T> modifier) {
        this(() -> {
            T ret = factory.get();
            modifier.accept(ret);
            return ret;
        });
    }

    public LazyValue(Supplier<T> factory) {
        this.factory = factory;
    }

    private final Supplier<T> factory;
    private T value;

    @Override
    public T get() {
        if (value == null) {
            value = Objects.requireNonNull(factory.get());
        }
        return value;
    }

}
