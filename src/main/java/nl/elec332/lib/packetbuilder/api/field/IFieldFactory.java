package nl.elec332.lib.packetbuilder.api.field;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;

import java.lang.annotation.Annotation;

/**
 * Created by Elec332 on 2/27/2021
 */
public interface IFieldFactory<A extends Annotation, T> {

    AbstractField<?> instantiate(A annotation, AbstractPacketObject packet, Class<T> type, ValueReference<T> fieldValue);

}
