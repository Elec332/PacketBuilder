package nl.elec332.lib.packetbuilder.impl.fields.numbers;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.IValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

/**
 * Created by Elec332 on 2/26/2021
 */
public class ByteField extends AbstractSimpleField<Byte> {

    public ByteField(IValueReference<Byte> reference) {
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
