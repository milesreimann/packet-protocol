package io.github.deroq42.packetsystem.core.connection;

import io.github.deroq42.packetsystem.api.connection.Connection;
import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.common.packet.codec.PacketFieldCodecs;
import io.github.deroq42.packetsystem.core.connection.model.ConnectionType;
import io.github.deroq42.packetsystem.core.packet.DefaultPacketRegistry;
import io.github.deroq42.packetsystem.core.packet.PacketBacklog;
import io.github.deroq42.packetsystem.core.packet.PacketFactory;
import io.github.deroq42.packetsystem.core.packet.PacketScanner;
import io.github.deroq42.packetsystem.core.packet.codec.DefaultPacketCodec;
import io.github.deroq42.packetsystem.core.packet.codec.PacketDecoder;
import io.github.deroq42.packetsystem.core.packet.codec.PacketEncoder;
import io.github.deroq42.packetsystem.core.packet.codec.PacketFieldRegistry;
import io.github.deroq42.packetsystem.core.packet.listener.DefaultPacketListenerRegistry;
import io.github.deroq42.packetsystem.core.pipeline.ConnectionHandler;
import io.netty.channel.ChannelFuture;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
public abstract class AbstractConnection implements Connection {
    @Getter
    private final @NotNull ConnectionType connectionType;
    @Getter
    private final @NotNull String host;
    @Getter
    private final int port;
    @Getter(value = AccessLevel.PROTECTED)
    private final @Nullable Consumer<Void> connectCallback;
    @Getter(value = AccessLevel.PROTECTED)
    private final @Nullable Consumer<Void> disconnectCallback;
    @Getter
    private final @NotNull PacketFactory packetFactory;
    @Getter
    private final @NotNull DefaultPacketRegistry packetRegistry;
    @Getter
    private final @NotNull DefaultPacketCodec packetCodec;
    @Getter
    private final @NotNull DefaultPacketListenerRegistry packetListenerRegistry;
    private final @NotNull PacketBacklog packetBacklog;
    @Getter(value = AccessLevel.PROTECTED)
    private final @NotNull ConnectionHandler connectionHandler;

    private final @NotNull AtomicBoolean connected = new AtomicBoolean(false);

    @Setter(value = AccessLevel.PROTECTED)
    private @Nullable ChannelFuture channelFuture;

    protected AbstractConnection(
        @NotNull ConnectionType connectionType,
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    ) {
        this.connectionType = connectionType;
        this.host = host;
        this.port = port;
        this.connectCallback = connectCallback;
        this.disconnectCallback = disconnectCallback;
        this.packetFactory = new PacketFactory();
        this.packetRegistry = new DefaultPacketRegistry(new PacketScanner());

        PacketFieldRegistry packetFieldRegistry = new PacketFieldRegistry();
        PacketEncoder packetEncoder = new PacketEncoder(this, packetFieldRegistry);
        PacketDecoder packetDecoder = new PacketDecoder(this, packetFieldRegistry);

        this.packetCodec = new DefaultPacketCodec(packetEncoder, packetDecoder, packetFieldRegistry);
        this.packetBacklog = new PacketBacklog(this);
        this.packetListenerRegistry = new DefaultPacketListenerRegistry();
        this.connectionHandler = new ConnectionHandler(this, _ -> packetBacklog.flush());

        packetCodec.addFieldCodec(PacketFieldCodecs.STRING_UTF8_CODEC);
        packetCodec.addFieldCodec(PacketFieldCodecs.INTEGER_CODEC);
        packetCodec.addFieldCodec(PacketFieldCodecs.UUID_CODEC);
        packetCodec.addFieldCodec(PacketFieldCodecs.BYTE_ARRAY_CODEC);

        Runtime.getRuntime().addShutdownHook(new Thread(this::forceClose, "connection-cleanup"));
    }

    protected AbstractConnection(@NotNull ConnectionType connectionType, @NotNull String host, int port) {
        this(connectionType, host, port, null, null);
    }

    public abstract void cleanUp();

    public abstract @NotNull Logger getLogger();

    @Override
    public void close() {
        if (channelFuture == null) {
            getLogger().warn("Failed to close connection {}: No open connection.", getClass());
            return;
        }

        forceClose();
    }

    @Override
    public void sendPacket(@NotNull Packet packet) {
        if (connectionHandler.getChannel() == null) {
            packetBacklog.add(packet);
            return;
        }

        connectionHandler.getChannel().writeAndFlush(packet);
        getLogger().info("Sent packet {}", packet.getUniqueId());
    }

    @Override
    public boolean isConnected() {
        return connected.get();
    }

    protected void setConnected(boolean b) {
        connected.set(b);
    }

    protected void waitForClose() throws InterruptedException {
        if (channelFuture == null) {
            setConnected(false);
            return;
        }

        channelFuture.channel().closeFuture().sync();
        handleClosedConnection();
    }

    protected void callConnectCallback() {
        if (connectCallback != null) {
            connectCallback.accept(null);
        }
    }

    protected void callDisconnectCallback() {
        if (disconnectCallback != null) {
            disconnectCallback.accept(null);
        }
    }

    private void forceClose() {
        try {
            if (channelFuture != null && channelFuture.channel().isOpen()) {
                channelFuture.channel().close().sync();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            cleanUp();
            handleClosedConnection();
        }
    }

    private void handleClosedConnection() {
        if (channelFuture == null) {
            return;
        }

        callDisconnectCallback();
        packetBacklog.clear();
        setChannelFuture(null);
        setConnected(false);

        getLogger().info("{} closed successfully", connectionType.getDisplayName());
    }
}
