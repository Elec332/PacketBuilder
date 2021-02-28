package nl.elec332.lib.packetbuilder.api;

import nl.elec332.lib.packetbuilder.AbstractField;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.util.SingleBiConsumer;

import java.util.function.BiConsumer;

/**
 * Created by Elec332 on 2/26/2021
 */
public interface IPayloadBinder<T extends AbstractPacketObject> {

    void bindValues(T packet, SingleBiConsumer<AbstractField<?>> comparator, BiConsumer<String, Object> comparator2);

}
