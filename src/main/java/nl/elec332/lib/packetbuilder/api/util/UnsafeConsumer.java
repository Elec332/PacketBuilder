package nl.elec332.lib.packetbuilder.api.util;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface UnsafeConsumer<T> {

    void accept(T t) throws Throwable;

    static <T> Consumer<T> wrap(UnsafeConsumer<T> consumer) {
        return v -> {
            try {
                consumer.accept(v);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        };
    }

}
