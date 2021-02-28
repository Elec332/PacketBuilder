package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleObjectField;

/**
 * Created by Elec332 on 2/27/2021
 */
public class ObjectField<O extends AbstractPacketObject> extends AbstractSimpleObjectField<AbstractPacketObject> {

    public ObjectField(AbstractPacketObject defaultValue) {
        super(defaultValue);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        value.serialize(buffer);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        value.deserialize(buffer);
    }

    @Override
    public int getObjectSize() {
        return value.getObjectSize();
    }

}
