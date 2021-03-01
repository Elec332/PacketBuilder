package nl.elec332.lib.packetbuilder.util;

/**
 * Created by Elec332 on 2/28/2021
 */
public class TypedValueReference<T> extends ValueReference<T> implements nl.elec332.lib.packetbuilder.api.util.TypedValueReference<T> {

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
