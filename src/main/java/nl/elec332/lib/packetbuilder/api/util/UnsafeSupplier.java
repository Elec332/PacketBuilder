package nl.elec332.lib.packetbuilder.api.util;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface UnsafeSupplier<T> {

    T get() throws Throwable;

    static <T> Supplier<T> wrap(UnsafeSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        };
    }

}
