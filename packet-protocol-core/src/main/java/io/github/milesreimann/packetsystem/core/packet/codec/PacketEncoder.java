package io.github.milesreimann.packetsystem.core.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.github.milesreimann.packetsystem.common.packet.codec.PacketFieldCodecs;
import io.github.milesreimann.packetsystem.core.connection.AbstractConnection;
import io.github.milesreimann.packetsystem.core.exception.PacketEncodeException;
import io.github.milesreimann.packetsystem.core.packet.codec.model.PacketFieldInfo;
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
public class PacketEncoder {
    private final AbstractConnection connection;
    private final PacketFieldRegistry packetFieldRegistry;

    public void encode(Packet packet, ByteBuf byteBuf) {
        if (packet.getUniqueId() == null) {
            throw new PacketEncodeException(packet, "Packet unique ID cannot be null");
        }

        if (!byteBuf.isWritable()) {
            throw new PacketEncodeException(packet, "ByteBuf is not writable");
        }

        Class<? extends Packet> packetClass = packet.getClass();
        UUID packetUniqueId = packet.getUniqueId();
        int initialWriterIndex = byteBuf.writerIndex();

        log.debug("Starting encoding of packet '{}' ({})", packetUniqueId, packetClass.getName());

        try {
            encodeHeader(packet, packetClass, byteBuf);
            encodeFields(packet, packetClass, byteBuf);

            int bytesWritten = byteBuf.writerIndex() - initialWriterIndex;

            log.debug(
                "Successfully encoded packet '{}'. {} bytes written",
                packet.getUniqueId(),
                bytesWritten
            );
        } catch (Exception e) {
            log.error("Failed to encode packet '{}'", packetUniqueId, e);
            byteBuf.writerIndex(initialWriterIndex);
            throw new PacketEncodeException(packet, e);
        }
    }

    private void encodeHeader(
        Packet packet,
        Class<? extends Packet> packetClass,
        ByteBuf byteBuf
    ) {
        int packetId = connection.getPacketRegistry().getPacketIdByClassOrThrow(packetClass);

        PacketFieldCodecs.INTEGER_CODEC.encode(packetId, byteBuf);
        log.trace("Encoded packet ID {} for packet '{}'", packetId, packet.getUniqueId());

        PacketFieldCodecs.UUID_CODEC.encode(packet.getUniqueId(), byteBuf);
        log.trace("Encoded packet UID '{}'", packet.getUniqueId());
    }

    private void encodeFields(
        Packet packet,
        Class<? extends Packet> packetClass,
        ByteBuf byteBuf
    ) {
        packetFieldRegistry.getPacketFieldInfo(packetClass).forEach(fieldMetadata ->
            encodeField(packet, packetClass, fieldMetadata, byteBuf)
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void encodeField(
        Packet packet,
        Class<? extends Packet> packetClass,
        PacketFieldInfo fieldInfo,
        ByteBuf byteBuf
    ) {
        PacketFieldCodec codec = packetFieldRegistry.getFieldCodec(fieldInfo.codecClass(), packetClass);

        Object value = Reflections.readField(fieldInfo.field(), packet);
        codec.encode(value, byteBuf);

        log.trace("Encoded field '{}' for packet '{}'", fieldInfo.field().getName(), packet.getUniqueId());
    }
}
