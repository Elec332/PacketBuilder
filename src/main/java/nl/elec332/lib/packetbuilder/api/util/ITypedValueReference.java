package nl.elec332.lib.packetbuilder.api.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface ITypedValueReference<T> extends IValueReference<T> {

    Class<T> getType();

    static <T> ITypedValueReference<T> wrap(final Class<T> type, final Supplier<T> supplier, final Consumer<T> consumer) {
        return new ITypedValueReference<T>() {

            @Override
            public Class<T> getType() {
                return type;
            }

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
