package io.github.milesreimann.packetsystem.core.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.github.milesreimann.packetsystem.common.packet.codec.PacketFieldCodecs;
import io.github.milesreimann.packetsystem.core.connection.AbstractConnection;
import io.github.milesreimann.packetsystem.core.exception.PacketDecodeException;
import io.github.milesreimann.packetsystem.core.packet.codec.model.PacketHeader;
import io.github.milesreimann.packetsystem.core.util.Reflections;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Log4j2
public class PacketDecoder {
    private final AbstractConnection connection;
    private final PacketFieldRegistry packetFieldRegistry;

    public Packet decode(ByteBuf byteBuf) throws PacketDecodeException {
        if (!byteBuf.isReadable()) {
            throw new PacketDecodeException("ByteBuf is not readable");
        }

        int initialReaderIndex = byteBuf.readerIndex();
        log.debug("Starting packet decoding from buffer at index {}", initialReaderIndex);

        try {
            PacketHeader header = decodeHeader(byteBuf);
            Packet packet = createAndPopulatePacket(header, byteBuf);
            int bytesRead = byteBuf.readerIndex() - initialReaderIndex;

            log.debug(
                "Successfully decoded packet '{}'. {} bytes read",
                packet.getUniqueId(),
                bytesRead
            );

            return packet;
        } catch (Exception e) {
            log.error("Failed to decode packet at buffer index {}", initialReaderIndex, e);
            byteBuf.readerIndex(initialReaderIndex);
            throw new PacketDecodeException(e);
        }
    }

    private Packet createAndPopulatePacket(PacketHeader header, ByteBuf byteBuf) {
        Class<? extends Packet> packetClass = connection.getPacketRegistry().getPacketClassByIdOrThrow(header.packetId());
        Packet packet = createPacketInstance(packetClass, header.uniqueId());

        log.debug("Decoding fields for packet '{}'", packetClass.getName());
        decodePacketFields(packet, packetClass, byteBuf);

        return packet;
    }

    private PacketHeader decodeHeader(ByteBuf byteBuf) throws PacketDecodeException {
        Integer packetId = PacketFieldCodecs.INTEGER_CODEC.decode(byteBuf);
        if (packetId == null) {
            throw new PacketDecodeException("Decoded packetId is null");
        }

        UUID packetUniqueId = PacketFieldCodecs.UUID_CODEC.decode(byteBuf);
        if (packetUniqueId == null) {
            throw new PacketDecodeException("Decoded packet UUID is null");
        }

        log.trace("Decoded packet header: ID={}, UID={}", packetId, packetUniqueId);
        return new PacketHeader(packetId, packetUniqueId);
    }

    private void decodePacketFields(
        Packet packet,
        Class<? extends Packet> packetClass,
        ByteBuf byteBuf
    ) {
        packetFieldRegistry.getPacketFieldInfo(packetClass).forEach(fieldMetadata -> {
            PacketFieldCodec<?> fieldCodec = packetFieldRegistry.getFieldCodec(fieldMetadata.codecClass(), packetClass);
            Object value = fieldCodec.decode(byteBuf);

            Reflections.writeField(fieldMetadata.field(), packet, value);
        });
    }

    private Packet createPacketInstance(Class<? extends Packet> packetClass, UUID uniqueId) {
        Packet packet = connection.getPacketFactory().create(packetClass);
        packet.setUniqueId(uniqueId);
        return packet;
    }
}
