package io.github.deroq42.packetsystem.api.connection;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.listener.PacketListenerRegistry;
import io.github.deroq42.packetsystem.api.packet.PacketRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface Connection {
    void open();

    void close();

    void sendPacket(@NotNull Packet packet);

    boolean isConnected();

    @NotNull String getHost();

    int getPort();

    @NotNull PacketRegistry getPacketRegistry();

    @NotNull PacketListenerRegistry getPacketListenerRegistry();
}