package nl.elec332.lib.packetbuilder.impl.fields.base;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.TypedValueReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;

/**
 * Created by Elec332 on 3/3/2021
 */
public class NullableObjectField extends ObjectField {

    public NullableObjectField(ValueReference<AbstractPacketObject> reference) {
        super(reference);
    }

    @Override
    public void deserialize(ByteBuf buffer) {
        try {
            set(((TypedValueReference<AbstractPacketObject>) valueReference).getType().getConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize new object!", e);
        }
        super.deserialize(buffer);
    }

}
