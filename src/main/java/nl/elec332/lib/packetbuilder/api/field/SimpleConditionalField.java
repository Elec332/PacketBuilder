package nl.elec332.lib.packetbuilder.api.field;

import nl.elec332.lib.packetbuilder.api.util.FieldWrapper;

import java.lang.annotation.*;

/**
 * Created by Elec332 on 2/27/2021
 */
@FieldWrapper
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleConditionalField {

    String method() default "";

    long numberValue() default Long.MIN_VALUE;

}
