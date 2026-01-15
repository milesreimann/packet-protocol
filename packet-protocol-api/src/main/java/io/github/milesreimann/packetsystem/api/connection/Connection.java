package io.github.milesreimann.packetsystem.api.connection;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.api.packet.listener.PacketListenerRegistry;
import io.github.milesreimann.packetsystem.api.packet.registry.PacketRegistry;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface Connection {
    void open();

    void close();

    void sendPacket(Packet packet);

    boolean isConnected();

    String getHost();

    int getPort();

    PacketRegistry getPacketRegistry();

    PacketListenerRegistry getPacketListenerRegistry();
}