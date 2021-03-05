package nl.elec332.lib.packetbuilder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import nl.elec332.lib.packetbuilder.api.ISerializableObject;
import nl.elec332.lib.packetbuilder.api.util.UnsafeConsumer;
import nl.elec332.lib.packetbuilder.impl.packet.NoPayloadPacket;
import nl.elec332.lib.packetbuilder.impl.packet.RawPayloadPacket;
import nl.elec332.lib.packetbuilder.internal.PacketDecoder;
import nl.elec332.lib.packetbuilder.internal.PacketFieldManager;
import nl.elec332.lib.packetbuilder.internal.PacketPayloadManager;
import nl.elec332.lib.packetbuilder.util.reflection.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 2/25/2021
 */
@SuppressWarnings("unused")
public abstract class AbstractPacketObject implements ISerializableObject {

    public AbstractPacketObject(String name) {
        this.name = name;
        this.fieldsFetched = false;
        this.fieldFactories = new HashMap<>();
        this.payload = null;
    }

    protected final String name;
    private final Map<Field, Function<Object, AbstractField<?>>> fieldFactories;
    private Map<String, AbstractField<?>> fields;
    private boolean fieldsFetched;
    private AbstractPacketObject payload;
    private AbstractPacketObject parent;

    protected <T> void registerField(String fieldName, Class<T> fieldType, Supplier<AbstractField<T>> factory) {
        registerField(fieldName, fieldType, v -> factory.get());
    }

    @SuppressWarnings("unchecked")
    protected <T> void registerField(String fieldName, Class<T> fieldType, Function<T, AbstractField<T>> factory) {
        Field f;
        try {
            f = ReflectionHelper.getField(getClass(), fieldName);
            if (f.getType() != fieldType) {
                throw new NoSuchFieldException("Invalid field type!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fieldFactories.put(f, (Function<Object, AbstractField<?>>) (Function<?, ?>) factory);
    }

    protected final void forFields(BiConsumer<String, AbstractField<?>> consumer) {
        streamFields().forEach(e -> consumer.accept(e.getKey(), e.getValue()));
    }

    protected final Stream<Map.Entry<String, AbstractField<?>>> streamFields() {
        return getAllFields().entrySet().stream().filter(f -> f.getValue().isValid());
    }

    public synchronized Map<String, AbstractField<?>> getAllFields() {
        checkFields();
        return this.fields;
    }

    protected void checkFields() {
        if (fieldsFetched) {
            return;
        }
        this.fields = Collections.unmodifiableMap(PacketFieldManager.INSTANCE.getFields(this, fieldFactories));
        fieldsFetched = true;
    }

    protected AbstractPacketObject getParent() {
        return parent;
    }

    @Override
    public final void serialize(ByteBuf buffer) {
        checkFields();
        ByteBuf payload = Unpooled.buffer();
        serializePayload(payload);
        beforeSerialization(payload);
        forFields((name, field) -> {
            try {
                field.serialize(buffer);
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize field: " + name, e);
            }
        });
        buffer.writeBytes(payload);
    }

    @Override
    public final void deserialize(ByteBuf buffer) {
        int index = buffer.readerIndex();
        forFields((name, field) -> {
            try {
                field.deserialize(buffer);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize field: " + name + " in " + AbstractPacketObject.this.getClass(), e);
            }
        });
        deserializePayload(buffer);
        deserializeTrailer(buffer);
        int lastIndex = buffer.readerIndex();
        afterDeSerialization(buffer.readerIndex(index));
        buffer.readerIndex(lastIndex);
    }

    protected void beforeSerialization(ByteBuf payload) {
    }

    protected void afterDeSerialization(ByteBuf buffer) {
    }

    @Override
    public int getObjectSize() {
        return getPacketSize() + Math.max(0, getPayloadLength()) + Math.max(0, getTrailerLength());
    }

    public int getPacketSize() {
        return streamFields()
                .map(Map.Entry::getValue)
                .mapToInt(AbstractField::getObjectSize)
                .sum();
    }

    protected int getTrailerLength() {
        return -1;
    }

    protected void deserializeTrailer(ByteBuf buffer) {
        if (getPayloadLength() == -1) {
            buffer.skipBytes(buffer.readableBytes());
        }
    }

    protected int getPayloadLength() {
        return (parent == null ? 0 : Math.max(0, parent.getPayloadLength() - getPacketSize())) - Math.max(0, getTrailerLength());
    }

    @SuppressWarnings("ConstantConditions")
    protected void deserializePayload(ByteBuf buffer) {
        if (buffer.readableBytes() == 0) {
            return;
        }
        AbstractPacketObject next = PacketPayloadManager.INSTANCE.getPayload(this, buffer.slice());
        if (next != null) {
            payload = next;
            payload.parent = this;
            ByteBuf payBuf;
            int payLen = getPayloadLength();
            if (payLen >= 0) {
                if (payLen == 0) {
                    payBuf = Unpooled.EMPTY_BUFFER;
                } else {
                    payBuf = buffer.readSlice(payLen);
                }
                PacketDecoder.decode(payBuf, payload);
            } else {
                int trailLen = getTrailerLength();
                if (trailLen >= 0) {
                    payBuf = buffer.readSlice(buffer.readableBytes() - trailLen);
                } else {
                    payBuf = buffer;
                }
                payload.deserialize(payBuf);
            }
        } else {
            payload = new NoPayloadPacket();
        }
    }

    protected void serializePayload(ByteBuf buffer) {
        if (payload == null) {
            return;
        }
        payload.serialize(buffer);
    }

    protected void addPayload(byte[] data) {
        this.addPayload(new RawPayloadPacket(), d -> d.data = data);
    }

    public <P extends AbstractPacketObject> void addPayloadT(P data, UnsafeConsumer<P> modifier) {
        addPayload(data, UnsafeConsumer.wrap(modifier));
    }

    public <P extends AbstractPacketObject> void addPayload(P data, Consumer<P> modifier) {
        modifier.accept(data);
        addPayload(data);
    }

    public void addPayload(AbstractPacketObject data) {
        if (this.payload == null) {
            this.payload = data;
            PacketPayloadManager.INSTANCE.addPayload(this, data);
        } else {
            this.payload.addPayload(payload);
        }
    }

    public <T extends AbstractPacketObject> boolean hasPayload(Class<T> layerType) {
        return getPayload(layerType) != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractPacketObject> T getPayload(Class<T> payloadType) {
        if (payload == null || payloadType.isAssignableFrom(payload.getClass())) {
            return (T) payload;
        }
        return payload.getPayload(payloadType);
    }

    @Override
    public String toString() {
        StringBuilder fields = new StringBuilder();
        streamFields().filter(e -> !e.getValue().isHidden()).forEach(entry -> fields.append(entry.getKey()).append(" = ").append(entry.getValue().toString()).append(", "));
        if (payload != null && !(payload instanceof NoPayloadPacket)) {
            fields.append("payload = ")
                    .append(payload);
        } else if (fields.length() > 0) {
            fields.delete(fields.length() - 2, fields.length());
        }
        if (fields.length() > 0) {
            fields.reverse().append("{").reverse().append("}");
        }
        return name + fields;
    }

}
