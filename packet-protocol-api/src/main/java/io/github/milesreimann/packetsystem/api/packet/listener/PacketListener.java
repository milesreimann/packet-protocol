package io.github.milesreimann.packetsystem.api.packet.listener;

import io.github.milesreimann.packetsystem.api.connection.Connection;
import io.github.milesreimann.packetsystem.api.packet.Packet;

/**
 * @author Miles
 * @since 28.08.25
 */
@FunctionalInterface
public interface PacketListener<P extends Packet> {
    void handle(P packet, Connection connection);
}
