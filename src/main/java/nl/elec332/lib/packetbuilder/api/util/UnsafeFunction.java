package nl.elec332.lib.packetbuilder.api.util;

import java.util.function.Function;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface UnsafeFunction<T, R> {

    R apply(T t) throws Throwable;

    static <T, R> Function<T, R> wrap(UnsafeFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable thr) {
                throw new RuntimeException(thr);
            }
        };
    }

}
