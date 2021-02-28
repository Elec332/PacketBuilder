package nl.elec332.lib.packetbuilder.api;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 2/26/2021
 */
public interface IPacketPayloadManager {

    ///////////////////////////////
    // Simple methods
    ///////////////////////////////

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper) {
        bindLayers(lower, upper, o -> true);
    }

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, Predicate<LOWER> predicate) {
        bindLayers(lower, upper, predicate, (a, b) -> {
        });
    }

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, BiPredicate<LOWER, ByteBuf> predicate) {
        bindLayers(lower, upper, predicate, (a, b) -> {
        });
    }

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, BiConsumer<LOWER, UPPER> payloadHandler) {
        bindLayers(lower, upper, (packet, buf) -> true, payloadHandler);
    }

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, Consumer<LOWER> payloadHandler) {
        bindLayers(lower, upper, (packet, buf) -> true, payloadHandler);
    }

    ///////////////////////////////
    // Derived methods
    ///////////////////////////////

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, Predicate<LOWER> predicate, Consumer<LOWER> payloadHandler) {
        bindLayers(lower, upper, predicate, (l, u) -> payloadHandler.accept(l));
    }

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, BiPredicate<LOWER, ByteBuf> predicate, Consumer<LOWER> payloadHandler) {
        bindLayers(lower, upper, predicate, (l, u) -> payloadHandler.accept(l));
    }

    default <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, Predicate<LOWER> predicate, BiConsumer<LOWER, UPPER> payloadHandler) {
        bindLayers(lower, upper, (packet, buf) -> predicate.test(packet), payloadHandler);
    }

    ///////////////////////////////
    // Root methods
    ///////////////////////////////

    <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, IPayloadBinder<LOWER> payloadBinder);

    <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, BiPredicate<LOWER, ByteBuf> predicate, BiConsumer<LOWER, UPPER> payloadHandler);

    AbstractPacketObject getPayload(AbstractPacketObject packet, ByteBuf peekBuffer);

    void addPayload(AbstractPacketObject packet, AbstractPacketObject payload);

}
