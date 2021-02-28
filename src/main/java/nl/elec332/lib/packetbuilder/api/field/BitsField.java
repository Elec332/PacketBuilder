package nl.elec332.lib.packetbuilder.api.field;

import nl.elec332.lib.packetbuilder.impl.fields.AbstractBitField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 2/27/2021
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BitsField {

    Class<? extends AbstractBitField> value();

    int bits();

    int startBit();

}
