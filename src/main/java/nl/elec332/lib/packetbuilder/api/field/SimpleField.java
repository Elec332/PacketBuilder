package nl.elec332.lib.packetbuilder.api.field;

import nl.elec332.lib.packetbuilder.AbstractField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 2/27/2021
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleField {

    Class<? extends AbstractField> value();

}
