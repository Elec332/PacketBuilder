package nl.elec332.lib.packetbuilder.api;

import nl.elec332.lib.packetbuilder.api.field.IFieldFactory;

import java.lang.annotation.Annotation;

/**
 * Created by Elec332 on 2/28/2021
 */
public interface IPacketFieldManager {

    <A extends Annotation> void registerFieldFactory(Class<A> annotation, IFieldFactory<A, ?> factory);

}
