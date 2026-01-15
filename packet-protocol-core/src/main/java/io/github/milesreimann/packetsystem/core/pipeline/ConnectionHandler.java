package io.github.milesreimann.packetsystem.core.pipeline;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.core.connection.AbstractConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ConnectionHandler extends SimpleChannelInboundHandler<Packet> {
    private final AbstractConnection connection;
    private final Consumer<ConnectionHandler> channelActiveCallback;

    @Getter
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        channelActiveCallback.accept(this);

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.channel = null;
        super.channelInactive(ctx);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        connection.getPacketListenerRegistry().getPacketListeners(packet.getClass()).forEach(listener -> listener.handle(packet, connection));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        connection.getLogger().error(cause);
    }
}
