package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractVarLengthField;

/**
 * Created by Elec332 on 3/3/2021
 */
public class SizedObjectField extends AbstractVarLengthField<AbstractPacketObject> {

    public SizedObjectField(ValueReference<AbstractPacketObject> reference, IntReference length) {
        super(reference, length);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        AbstractPacketObject object = get();
        object.serialize(buffer);
        setLength(object.getObjectSize());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        buffer = buffer.readSlice(getLength());
        get().deserialize(buffer);
        if (buffer.readableBytes() > 0) {
            throw new RuntimeException("Object of type " + get().getClass() + " didn't deserialize the entire buffer, bytes left: " + buffer.readableBytes());
        }
    }

    @Override
    public int getObjectSize() {
        return get().getObjectSize();
    }

}
