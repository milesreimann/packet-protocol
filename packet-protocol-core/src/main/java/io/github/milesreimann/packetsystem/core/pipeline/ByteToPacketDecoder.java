package io.github.milesreimann.packetsystem.core.pipeline;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.core.connection.AbstractConnection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
public class ByteToPacketDecoder extends ByteToMessageDecoder {
    private final AbstractConnection connection;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        if (byteBuf == null || byteBuf instanceof EmptyByteBuf) {
            return;
        }

        Packet packet = connection.getPacketCodec().decode(byteBuf);
        list.add(packet);
    }
}
