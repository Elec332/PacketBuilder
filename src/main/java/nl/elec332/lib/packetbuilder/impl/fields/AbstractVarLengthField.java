package nl.elec332.lib.packetbuilder.impl.fields;

import java.util.function.IntSupplier;

/**
 * Created by Elec332 on 2/27/2021
 */
public abstract class AbstractVarLengthField<T> extends AbstractSimpleObjectField<T> {

    public AbstractVarLengthField(T defaultValue, IntSupplier length) {
        super(defaultValue);
        this.length = length;
    }

    protected final IntSupplier length;

}
