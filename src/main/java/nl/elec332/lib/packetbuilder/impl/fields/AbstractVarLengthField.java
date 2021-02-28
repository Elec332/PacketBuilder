package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.api.util.IValueReference;

import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 2/27/2021
 */
public abstract class AbstractVarLengthField<T> extends AbstractSimpleField<T> {

    public AbstractVarLengthField(IValueReference<T> reference, IntSupplier length) {
        super(reference);
        this.length = length;
    }

    protected final IntSupplier length;

}
