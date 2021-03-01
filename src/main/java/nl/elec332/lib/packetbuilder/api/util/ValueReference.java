package nl.elec332.lib.packetbuilder.api.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface ValueReference<T> extends Supplier<T>, Consumer<T> {

    @Override
    T get();

    default boolean hasSetter() {
        return true;
    }

    @Override
    void accept(T t);

    @SuppressWarnings("unchecked")
    default <T2> ValueReference<T2> cast() {
        return (ValueReference<T2>) this;
    }

    static <T> ValueReference<T> wrap(final Supplier<T> supplier, final Consumer<T> consumer) {
        return new ValueReference<T>() {

            @Override
            public T get() {
                return supplier.get();
            }

            @Override
            public void accept(T t) {
                if (consumer == null) {
                    throw new RuntimeException("No setter present!");
                }
                consumer.accept(t);
            }

            @Override
            public boolean hasSetter() {
                return consumer != null;
            }

        };
    }

}
