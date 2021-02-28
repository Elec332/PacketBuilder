package nl.elec332.lib.packetbuilder.impl.fields;

import nl.elec332.lib.packetbuilder.AbstractField;

/**
 * Created by Elec332 on 2/26/2021
 */
public abstract class AbstractSimpleObjectField<T> extends AbstractField {

    public AbstractSimpleObjectField(T defaultValue) {
        this.value = defaultValue;
    }

    public T value;

}
