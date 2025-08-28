package io.github.deroq42.packetsystem.core.connection;

import io.github.deroq42.packetsystem.core.connection.model.ConnectionType;
import io.github.deroq42.packetsystem.core.pipeline.Pipeline;
import io.github.deroq42.packetsystem.core.util.NettyUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
public class ServerConnection extends AbstractConnection {
    private @Nullable EventLoopGroup bossGroup;
    private @Nullable EventLoopGroup workerGroup;

    public ServerConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    ) {
        super(ConnectionType.SERVER, host, port, connectCallback, disconnectCallback);
    }

    public ServerConnection(@NotNull String host, int port) {
        super(ConnectionType.SERVER, host, port);
    }

    @Override
    public void open() {
        this.bossGroup = NettyUtils.getAvailableEventLoopGroup();
        this.workerGroup = NettyUtils.getAvailableEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap()
            .group(bossGroup, workerGroup)
            .channel(NettyUtils.getServerChannelClass())
            .option(ChannelOption.SO_BACKLOG, 128)
            .option(ChannelOption.SO_REUSEADDR, true)
            .option(ChannelOption.SO_RCVBUF, 65536)
            .option(ChannelOption.SO_SNDBUF, 65536)
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.SO_RCVBUF, 65536)
            .childOption(ChannelOption.SO_SNDBUF, 65536)
            .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true))
            .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) {
                    Pipeline.init(
                        ServerConnection.this,
                        channel.pipeline(),
                        getConnectionHandler()
                    );
                }
            });

        try {
            startServer(serverBootstrap);
            waitForClose();
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void cleanUp() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            this.bossGroup = null;
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            this.workerGroup = null;
        }
    }

    @Override
    public @NotNull Logger getLogger() {
        return log;
    }

    private void startServer(@NotNull ServerBootstrap bootstrap) throws InterruptedException {
        String host = getHost();
        int port = getPort();

        log.info("Starting server on {}:{}", host, port);

        ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
        setChannelFuture(channelFuture);
        callConnectCallback();
        setConnected(true);

        log.info("Server started and listening on {}:{}.", host, port);
    }
}