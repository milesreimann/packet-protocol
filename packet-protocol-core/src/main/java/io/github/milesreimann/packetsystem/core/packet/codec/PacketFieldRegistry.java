package io.github.milesreimann.packetsystem.core.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.api.packet.codec.PacketField;
import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.github.milesreimann.packetsystem.core.packet.codec.model.PacketFieldInfo;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
public class PacketFieldRegistry {
    @SuppressWarnings("rawtypes")
    private final Map<Class<? extends PacketFieldCodec>, PacketFieldCodec> codecRegistry = new ConcurrentHashMap<>();
    private final Map<Class<? extends Packet>, List<PacketFieldInfo>> fieldCache = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    public <T> boolean registerFieldCodec(PacketFieldCodec<T> codec) {
        Class<? extends PacketFieldCodec> codecClass = codec.getClass();
        PacketFieldCodec<?> existingCodec = codecRegistry.put(codecClass, codec);

        if (existingCodec != null) {
            log.warn("Replaced existing field codec '{}' with new instance", codecClass.getName());
            return true;
        }

        log.debug("Registered field codec '{}'", codecClass.getName());
        return false;
    }

    public <P extends Packet> List<PacketFieldInfo> getPacketFieldInfo(Class<P> packetClass) {
        return fieldCache.computeIfAbsent(packetClass, _ -> Arrays.stream(packetClass.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(PacketField.class))
            .map(f -> new PacketFieldInfo(f, f.getAnnotation(PacketField.class).codec()))
            .toList()
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> PacketFieldCodec<T> getFieldCodec(
        Class<? extends PacketFieldCodec> codecClass,
        Class<? extends Packet> packetClass
    ) {
        PacketFieldCodec<T> fieldCodec = codecRegistry.get(codecClass);

        if (fieldCodec == null) {
            throw new NoSuchElementException(String.format(
                "Codec '%s', which is required by packet '%s', was not registered.",
                codecClass.getName(),
                packetClass.getName()
            ));
        }

        return fieldCodec;
    }
}
