package nl.elec332.lib.packetbuilder.fields.generic;

import nl.elec332.lib.packetbuilder.api.field.PacketFieldWrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 2/27/2021
 */
@PacketFieldWrapper
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleConditionalField {

    String method() default "";

    long numberValue() default Long.MIN_VALUE;

}
