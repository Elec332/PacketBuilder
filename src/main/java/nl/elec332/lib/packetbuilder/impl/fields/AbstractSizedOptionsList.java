package nl.elec332.lib.packetbuilder.impl.fields;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 3/1/2021
 */
public abstract class AbstractSizedOptionsList<T extends AbstractPacketObject> extends AbstractListField<T> {

    public AbstractSizedOptionsList(ValueReference<List<T>> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        for (T t : get()) {
            t.serialize(buffer);
        }
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        int count = 0;
        int max = expectedListSize();
        set(new ArrayList<>());
        while (max - count > 0) {
            T t = getNextType(buffer.slice());
            t.deserialize(buffer);
            count += t.getObjectSize();
            add(t);
        }
    }

    protected abstract int expectedListSize();

    protected abstract T getNextType(ByteBuf buffer);

    @Override
    public int getObjectSize() {
        return get().stream().mapToInt(AbstractPacketObject::getObjectSize).sum();
    }

}