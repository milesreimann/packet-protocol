package io.github.deroq42.packetsystem.core.util;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NettyUtils {
    @SuppressWarnings("deprecation")
    public static @NotNull EventLoopGroup getAvailableEventLoopGroup() {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup();
        } else if (KQueue.isAvailable()) {
            return new KQueueEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    public static @NotNull Class<? extends ServerChannel> getServerChannelClass() {
        if (Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        } else if (KQueue.isAvailable()) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    public static @NotNull Class<? extends Channel> getClientChannelClass() {
        if (Epoll.isAvailable()) {
            return EpollSocketChannel.class;
        } else if (KQueue.isAvailable()) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }
}