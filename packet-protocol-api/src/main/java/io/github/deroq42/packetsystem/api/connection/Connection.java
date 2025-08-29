package io.github.deroq42.packetsystem.api.connection;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.listener.PacketListenerRegistry;
import io.github.deroq42.packetsystem.api.packet.registry.PacketRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a network connection capable of sending and receiving {@link Packet}s.
 * <p>
 * A connection abstracts the underlying transport layer and provides access
 * to packet registries and listener registries for protocol handling.
 * </p>
 * <p>
 * Implementations are responsible for handling the lifecycle of the connection
 * (open, close, cleanup) and maintaining its state.
 *
 * @author Miles
 * @since 28.08.25
 */
public interface Connection {
    /**
     * Opens the connection and establishes the underlying transport.
     * <p>
     * This method initializes the connection and makes it ready for sending
     * and receiving packets. If the connection is already open, this call
     * has no effect.
     * </p>
     */
    void open();

    /**
     * Closes the connection gracefully, releasing any underlying resources.
     * <p>
     * If the connection is not currently open, this call has no effect.
     * All pending packets may be lost upon closing.
     * </p>
     */
    void close();

    /**
     * Sends a {@link Packet} through the connection.
     * <p>
     * If the connection is not ready, the packet may be queued or dropped,
     * depending on the implementation.
     * </p>
     *
     * @param packet the non-null packet to be sent
     */
    void sendPacket(@NotNull Packet packet);

    /**
     * Checks whether the connection is currently active and usable.
     *
     * @return {@code true} if the connection is open and active, otherwise {@code false}
     */
    boolean isConnected();

    /**
     * Gets the hostname or IP address associated with this connection.
     *
     * @return the remote host, never {@code null}
     */
    @NotNull String getHost();

    /**
     * Gets the port number used by this connection.
     *
     * @return the remote port
     */
    int getPort();

    /**
     * Provides access to the packet registry associated with this connection.
     * <p>
     * The packet registry contains information about which packet IDs map to
     * which packet classes and is used during encoding and decoding.
     * </p>
     *
     * @return the packet registry, never {@code null}
     */
    @NotNull PacketRegistry getPacketRegistry();

    /**
     * Provides access to the packet listener registry associated with this connection.
     * <p>
     * The listener registry allows registration and invocation of listeners
     * for incoming packets.
     * </p>
     *
     * @return the packet listener registry, never {@code null}
     */
    @NotNull PacketListenerRegistry getPacketListenerRegistry();
}