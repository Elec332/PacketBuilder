package nl.elec332.lib.packetbuilder.fields.generic;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.api.field.PacketFieldWrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 3/5/2021
 */
@PacketFieldWrapper
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MappedField {

    String field();

    int[] values();

    Class<? extends AbstractField<?>>[] valueMap();

}
