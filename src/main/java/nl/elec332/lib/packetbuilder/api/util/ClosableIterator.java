package nl.elec332.lib.packetbuilder.api.util;

import java.util.Iterator;

/**
 * Created by Elec332 on 3/1/2021
 */
public interface ClosableIterator<T> extends Iterator<T> {

    @Override
    boolean hasNext();

    @Override
    T next();

    void close();

}
