package nl.elec332.lib.packetbuilder.fields;

import nl.elec332.lib.packetbuilder.api.field.PacketField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 2/28/2021
 */
@PacketField
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberField {

    Class<? extends Number> value() default Number.class;

}
