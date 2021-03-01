package nl.elec332.lib.packetbuilder.api.util;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 3/1/2021
 */
public interface IntReference extends IntSupplier, IntConsumer {

    @Override
    int getAsInt();

    default boolean hasSetter() {
        return true;
    }

    @Override
    void accept(int t);

    static IntReference wrap(final IntSupplier supplier, final IntConsumer consumer) {
        return new IntReference() {

            @Override
            public int getAsInt() {
                return supplier.getAsInt();
            }

            @Override
            public void accept(int i) {
                if (consumer == null) {
                    throw new RuntimeException("No setter present!");
                }
                consumer.accept(i);
            }

            @Override
            public boolean hasSetter() {
                return consumer != null;
            }

        };
    }

}
