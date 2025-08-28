package io.github.deroq42.packetsystem.core.pipeline;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.core.connection.AbstractConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
public class ByteToPacketDecoder extends ByteToMessageDecoder {
    private final @NotNull AbstractConnection connection;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf instanceof EmptyByteBuf) {
            return;
        }

        Packet packet = connection.getPacketCodec().decode(byteBuf);
        list.add(packet);
    }
}
