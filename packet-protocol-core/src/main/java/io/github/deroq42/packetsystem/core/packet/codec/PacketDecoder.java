package io.github.deroq42.packetsystem.core.packet.codec;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.github.deroq42.packetsystem.common.packet.codec.PacketFieldCodecs;
import io.github.deroq42.packetsystem.core.exception.PacketDecodeException;
import io.github.deroq42.packetsystem.core.packet.codec.model.PacketHeader;
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
public class PacketDecoder {
    private final @NotNull PacketFieldRegistry packetFieldRegistry;

    public @NotNull Packet decode(@NotNull ByteBuf byteBuf) {
        if (!byteBuf.isReadable()) {
            throw new PacketDecodeException("ByteBuf is not readable");
        }

        int initialReaderIndex = byteBuf.readerIndex();
        log.debug("Starting packet decoding from buffer at index {}", initialReaderIndex);

        try {
            PacketHeader header = decodePacketHeader(byteBuf);
            log.trace("Decoded packet header: ID={}, UID={}", header.packetId(), header.uniqueId());

            Class<? extends Packet> packetClass = getPacketClass(header.packetId());
            Packet packet = createPacketInstance(packetClass, header.uniqueId());

            log.debug("Decoding packet fields for '{}'", packetClass.getSimpleName());
            decodePacketFields(packet, packetClass, byteBuf);

            logDecodingSuccess(packet, byteBuf.readerIndex() - initialReaderIndex);
            return packet;
        } catch (Exception e) {
            log.error("Failed to decode packet at buffer index {}", initialReaderIndex, e);
            throw new PacketDecodeException(e);
        }
    }

    private @NotNull PacketHeader decodePacketHeader(@NotNull ByteBuf byteBuf) {
        Integer packetId = PacketFieldCodecs.INTEGER_CODEC.decode(byteBuf);
        if (packetId == null) {
            throw new PacketDecodeException("Decoded packetId is null");
        }

        UUID packetUniqueId = PacketFieldCodecs.UUID_CODEC.decode(byteBuf);
        if (packetUniqueId == null) {
            throw new PacketDecodeException("Decoded packet UUID is null");
        }

        return new PacketHeader(packetId, packetUniqueId);
    }

    private void decodePacketFields(
        @NotNull Packet packet,
        @NotNull Class<? extends Packet> packetClass,
        @NotNull ByteBuf byteBuf
    ) {
        packetFieldRegistry.getPacketFieldInfo(packetClass).forEach(fieldMetadata -> {
            PacketFieldCodec<?> fieldCodec = packetFieldRegistry.getFieldCodec(fieldMetadata.codecClass(), packetClass);
            Object value = fieldCodec.decode(byteBuf);

            Reflections.writeField(fieldMetadata.field(), packet, value);
        });
    }

    private void logDecodingSuccess(@NotNull Packet packet, int bytesRead) {
        log.debug(
            "Successfully decoded packet '{}'. {} bytes read",
            packet.getUniqueId(),
            bytesRead
        );
    }
}
