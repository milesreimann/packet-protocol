package io.github.deroq42.packetsystem.core.packet.codec;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.github.deroq42.packetsystem.common.packet.codec.PacketFieldCodecs;
import io.github.deroq42.packetsystem.core.exception.PacketEncodeException;
import io.github.deroq42.packetsystem.core.packet.util.Reflections;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Log4j2
public class PacketEncoder {
    private final @NotNull PacketFieldRegistry packetFieldRegistry;

    public void encode(@NotNull Packet packet, @NotNull ByteBuf byteBuf) {
        if (packet.getUniqueId() == null) {
            throw new IllegalArgumentException("Packet unique ID cannot be null");
        }

        if (!byteBuf.isWritable()) {
            throw new IllegalArgumentException("ByteBuf is not writable");
        }

        Class<? extends Packet> packetClass = packet.getClass();
        UUID packetUniqueId = packet.getUniqueId();
        int initialWriterIndex = byteBuf.writerIndex();

        log.debug("Starting encoding of packet '{}' ({})", packetUniqueId, packetClass.getSimpleName());

        try {
            encodePacketHeader(packet, packetClass, byteBuf);
            encodePacketFields(packet, packetClass, byteBuf);

            logEncodingSuccess(packet, byteBuf.writerIndex() - initialWriterIndex);
        } catch (Exception e) {
            log.error("Failed to encode packet '{}'", packetUniqueId, e);
            byteBuf.writerIndex(initialWriterIndex);
            throw new PacketEncodeException(packet, e);
        }
    }

    private void encodePacketHeader(
        @NotNull Packet packet,
        @NotNull Class<? extends Packet> packetClass,
        @NotNull ByteBuf byteBuf
    ) {
        int packetId = getNumericPacketId(packetClass);
        PacketFieldCodecs.INTEGER_CODEC.encode(packetId, byteBuf);
        log.trace("Encoded packet ID {} for packet '{}'", packetId, packet.getUniqueId());

        PacketFieldCodecs.UUID_CODEC.encode(packet.getUniqueId(), byteBuf);
        log.trace("Encoded packet UID '{}'", packet.getUniqueId());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void encodePacketFields(
        @NotNull Packet packet,
        @NotNull Class<? extends Packet> packetClass,
        @NotNull ByteBuf byteBuf
    ) {
        packetFieldRegistry.getPacketFieldInfo(packetClass).forEach(fieldMetadata -> {
            PacketFieldCodec fieldCodec = packetFieldRegistry.getFieldCodec(fieldMetadata.codecClass(), packetClass);
            Object value = Reflections.readField(fieldMetadata.field(), packet);
            fieldCodec.encode(value, byteBuf);

            log.trace("Encoded field '{}' for packet '{}'", fieldMetadata.field().getName(), packet.getUniqueId());
        });
    }

    private void logEncodingSuccess(@NotNull Packet packet, int bytesWritten) {
        log.debug(
            "Successfully encoded packet '{}'. {} bytes written",
            packet.getUniqueId(),
            bytesWritten
        );
    }
}
