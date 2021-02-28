package nl.elec332.lib.packetbuilder.api.util;

/**
 * Created by Elec332 on 2/26/2021
 */
public interface SingleBiConsumer<T> {

    void accept(T t1, T t2);

}
