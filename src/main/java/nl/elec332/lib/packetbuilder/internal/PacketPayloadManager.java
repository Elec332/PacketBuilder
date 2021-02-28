package nl.elec332.lib.packetbuilder.internal;

import io.netty.buffer.ByteBuf;
import nl.elec332.lib.packetbuilder.AbstractPacketObject;
import nl.elec332.lib.packetbuilder.api.IPacketPayloadManager;
import nl.elec332.lib.packetbuilder.api.IPayloadBinder;
import nl.elec332.lib.packetbuilder.impl.packet.NoPayloadPacket;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * Created by Elec332 on 2/26/2021
 */
public enum PacketPayloadManager implements IPacketPayloadManager {

    INSTANCE;

    PacketPayloadManager() {
        decodeLayers = new HashMap<>();
        encodeLayers = new HashMap<>();
    }

    private final Map<Class<? extends AbstractPacketObject>, Map<Class<? extends AbstractPacketObject>, BiPredicate<AbstractPacketObject, ByteBuf>>> decodeLayers;
    private final Map<Class<? extends AbstractPacketObject>, Map<Class<? extends AbstractPacketObject>, BiConsumer<AbstractPacketObject, AbstractPacketObject>>> encodeLayers;

    @Override
    public <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, IPayloadBinder<LOWER> payloadBinder) {
        bindLayers(lower, upper, (l, buf) -> {
            boolean[] ret = {true};
            payloadBinder.bindValues(l, (f1, f2) -> ret[0] |= Objects.equals(f1, f2), (s, o) -> ret[0] |= Objects.equals(((AbstractInternalField) Objects.requireNonNull(l.getAllFields().get(s))).getValue(), o));
            return ret[0];
        }, (l, u) -> {
            payloadBinder.bindValues(l, (f1, f2) -> ((AbstractInternalField) f1).importFrom(f2), (s, o) -> ((AbstractInternalField) Objects.requireNonNull(l.getAllFields().get(s))).setValue(o));
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <LOWER extends AbstractPacketObject, UPPER extends AbstractPacketObject> void bindLayers(Class<LOWER> lower, Class<UPPER> upper, BiPredicate<LOWER, ByteBuf> predicate, BiConsumer<LOWER, UPPER> payloadHandler) {
        decodeLayers.computeIfAbsent(lower, c -> new HashMap<>()).put(upper, Objects.requireNonNull((BiPredicate<AbstractPacketObject, ByteBuf>) predicate));
        encodeLayers.computeIfAbsent(lower, c -> new HashMap<>()).put(upper, Objects.requireNonNull((BiConsumer<AbstractPacketObject, AbstractPacketObject>) payloadHandler));
    }

    @Override
    public AbstractPacketObject getPayload(AbstractPacketObject packet, ByteBuf peekBuffer) {
        Map<Class<? extends AbstractPacketObject>,  BiPredicate<AbstractPacketObject, ByteBuf>> possibilities = decodeLayers.get(packet.getClass());
        if (possibilities == null || possibilities.isEmpty()) {
            return new NoPayloadPacket();
        }
        Class<? extends AbstractPacketObject> ret = null;
        for (Map.Entry<Class<? extends AbstractPacketObject>,  BiPredicate<AbstractPacketObject, ByteBuf>> entry : possibilities.entrySet()) {
            if (entry.getValue().test(packet, peekBuffer)) {
                if (ret == null) {
                    ret = entry.getKey();
                } else {
                    throw new RuntimeException("Multiple valid layers found when decoding " + packet.toString());
                }
            }
        }
        if (ret == null) {
            return new NoPayloadPacket();
        }
        try {
            return ret.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize packet " + ret, e);
        }
    }

    @Override
    public void addPayload(AbstractPacketObject packet, AbstractPacketObject payload) {
        try {
            encodeLayers.get(packet.getClass()).get(payload.getClass()).accept(packet, payload);
        } catch (NullPointerException e) {
            throw new RuntimeException("Cannot add payload " + payload + " to packet " + packet);
        }
    }

}
