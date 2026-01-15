package io.github.milesreimann.packetsystem.api.packet.listener;

import io.github.milesreimann.packetsystem.api.connection.Connection;
import io.github.milesreimann.packetsystem.api.packet.Packet;

/**
 * Listener for incoming {@link Packet}s of a specific type.
 * <p>
 * Implementations of this interface handle packets received on a
 * {@link Connection}. This interface is a {@link FunctionalInterface},
 * so it can be implemented with a lambda or method reference.
 * </p>
 *
 * @param <P> the type of packet this listener handles
 * @author Miles
 * @since 28.08.25
 */
@FunctionalInterface
public interface PacketListener<P extends Packet> {
    /**
     * Called when a packet of type {@code P} is received on the connection.
     *
     * @param packet     the received packet, not null
     * @param connection the connection on which the packet was received, not null
     */
    void handle(P packet, Connection connection);
}