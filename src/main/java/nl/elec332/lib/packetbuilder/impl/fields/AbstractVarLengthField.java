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

    protected final IntReference length;

}
