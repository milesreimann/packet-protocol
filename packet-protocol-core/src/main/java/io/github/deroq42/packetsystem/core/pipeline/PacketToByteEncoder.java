package io.github.deroq42.packetsystem.core.pipeline;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.core.connection.AbstractConnection;
import io.github.deroq42.packetsystem.core.exception.PacketEncodeException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Log4j2
public class PacketToByteEncoder extends MessageToByteEncoder<Packet> {
    private final @NotNull AbstractConnection connection;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf byteBuf) {
        try {
            connection.getPacketCodec().encode(packet, byteBuf);
        } catch (PacketEncodeException e) {
            log.error("Failed to encode packet '{}'", packet.getUniqueId(), e);
        }
    }
}