package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;
import nl.elec332.lib.packetbuilder.impl.fields.AbstractSimpleField;

/**
 * Created by Elec332 on 3/5/2021
 */
public class NullField extends AbstractSimpleField<Object> {

    public NullField(ValueReference<Object> reference) {
        super(reference);
    }

    @Override
    public void serialize(ByteBuf buffer) {
    }

    @Override
    public void deserialize(ByteBuf buffer) {
    }

    @Override
    public int getObjectSize() {
        return 0;
    }

    @Override
    protected boolean isHidden() {
        return true;
    }

}
