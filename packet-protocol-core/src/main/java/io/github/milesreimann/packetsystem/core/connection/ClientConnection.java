package io.github.milesreimann.packetsystem.core.connection;

import io.github.milesreimann.packetsystem.core.connection.model.ConnectionType;
import io.github.milesreimann.packetsystem.core.pipeline.Pipeline;
import io.github.milesreimann.packetsystem.core.util.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;


import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
public class ClientConnection extends AbstractConnection {
    private EventLoopGroup workerGroup;

    public ClientConnection(
        String host,
        int port,
        Consumer<Void> connectCallback,
        Consumer<Void> disconnectCallback
    ) {
        super(ConnectionType.CLIENT, host, port, connectCallback, disconnectCallback);
    }

    public ClientConnection(String host, int port) {
        super(ConnectionType.CLIENT, host, port);
    }

    @Override
    public void open() {
        this.workerGroup = NettyUtils.getAvailableEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
            .group(workerGroup)
            .channel(NettyUtils.getClientChannelClass())
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            .option(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    Pipeline.init(
                        ClientConnection.this,
                        channel.pipeline(),
                        getConnectionHandler()
                    );
                }
            });

        try {
            connectClient(bootstrap);
            waitForClose();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void cleanUp() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            this.workerGroup = null;
        }
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    private void connectClient(Bootstrap bootstrap) throws InterruptedException {
        String host = getHost();
        int port = getPort();

        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        setChannelFuture(channelFuture);
        callConnectCallback();
        setConnected(true);

        getLogger().info("Client connected to {}:{}.", host, port);
    }
}