package nl.elec332.lib.packetbuilder.util;

import nl.elec332.lib.packetbuilder.api.util.ITypedValueReference;

/**
 * Created by Elec332 on 2/28/2021
 */
public class TypedValueReference<T> extends ValueReference<T> implements ITypedValueReference<T> {

    public TypedValueReference(Class<T> type) {
        super();
        this.type = type;
    }

    public TypedValueReference(Class<T> type, T value) {
        super(value);
        this.type = type;
    }

    private final Class<T> type;

    @Override
    public Class<T> getType() {
        return type;
    }

}
