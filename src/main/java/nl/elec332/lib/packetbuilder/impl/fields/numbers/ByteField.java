package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractNumberField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ByteField extends AbstractNumberField<Byte> {

    public ByteField(ValueReference<Byte> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
        buffer.writeByte(get());
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        set(buffer.readByte());
    }


    @Override
    public int getObjectSize() {
        return 1;
    }

}
