package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.api.util.IntReference;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;

/**
 * Created by Elec332 on 2/27/2021
 */
public abstract class AbstractVarLengthField<T> extends AbstractSimpleField<T> {

    public AbstractVarLengthField(ValueReference<T> reference, IntReference length) {
        super(reference);
        this.length = length;
    }

    private final IntReference length;

    public int getLength() {
        return this.length.getAsInt();
    }

    protected void setLength(int length) {
        if (this.length.hasSetter()) {
            this.length.accept(length);
        }
    }

}
