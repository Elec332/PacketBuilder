package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

/**
 * Created by Elec332 on 2/27/2021
 */
public class ObjectField<O extends AbstractPacketObject> extends AbstractSimpleField<O> {

    public ObjectField(ValueReference<O> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        get().serialize(buffer);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        get().deserialize(buffer);
    }

    @Override
    public int getObjectSize() {
        return get().getObjectSize();
    }

}
