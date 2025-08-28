package io.github.deroq42.packetsystem.core.packet.codec;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.PacketField;
import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.github.deroq42.packetsystem.core.packet.codec.model.PacketFieldInfo;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

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
    private final @NotNull Map<Class<? extends PacketFieldCodec>, PacketFieldCodec> fieldCodecToCodecMap = new ConcurrentHashMap<>();
    private final @NotNull Map<Class<? extends Packet>, List<PacketFieldInfo>> packetFieldCache = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    public <T> boolean addFieldCodec(@NotNull PacketFieldCodec<T> codec) {
        Class<? extends PacketFieldCodec> codecClass = codec.getClass();
        PacketFieldCodec<?> existing = fieldCodecToCodecMap.put(codecClass, codec);

        if (existing != null) {
            log.warn("Replaced existing field codec '{}' with new instance", codecClass.getName());
            return true;
        }

        log.debug("Registered field codec '{}'", codecClass.getName());
        return false;
    }

    public @NotNull <P extends Packet> List<PacketFieldInfo> getPacketFieldInfo(@NotNull Class<P> packetClass) {
        return packetFieldCache.computeIfAbsent(packetClass, _ -> Arrays.stream(packetClass.getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(PacketField.class))
            .map(f -> new PacketFieldInfo(f, f.getAnnotation(PacketField.class).codec()))
            .toList()
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public @NotNull <T> PacketFieldCodec<T> getFieldCodec(
        @NotNull Class<? extends PacketFieldCodec> codecClass,
        @NotNull Class<? extends Packet> packetClass
    ) {
        PacketFieldCodec<T> fieldCodec = fieldCodecToCodecMap.get(codecClass);
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
