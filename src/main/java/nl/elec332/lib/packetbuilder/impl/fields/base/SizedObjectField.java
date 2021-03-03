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
        length.accept(object.getObjectSize());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        buffer = buffer.readSlice(length.getAsInt());
        get().deserialize(buffer);
        if (buffer.readableBytes() > 0) {
            throw new RuntimeException();
        }
    }

    @Override
    public int getObjectSize() {
        return get().getObjectSize();
    }

}
