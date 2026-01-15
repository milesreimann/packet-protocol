package io.github.milesreimann.packetsystem.api.packet.listener;

import io.github.milesreimann.packetsystem.api.packet.Packet;

import java.util.Collection;

/**
 * Registry for managing {@link PacketListener}s.
 * <p>
 * Provides methods to register, unregister, and retrieve listeners
 * for specific packet types.
 * </p>
 * <p>
 * Listener registration is type-safe via generics, ensuring that
 * listeners only receive the packet types they expect.
 * </p>
 *
 * @author Miles
 * @since 28.08.25
 */
public interface PacketListenerRegistry {
    /**
     * Registers a listener for a specific packet type.
     *
     * @param listener the listener to register, not null
     * @param <P>      the packet type handled by the listener
     */
    <P extends Packet> void registerPacketListener(PacketListener<P> listener);

    /**
     * Unregisters a previously registered listener for a specific packet type.
     *
     * @param packetClass the class of the packet the listener handles, not null
     * @param listener    the listener to remove, not null
     * @param <P>         the packet type handled by the listener
     */
    <P extends Packet> void unregisterPacketListener(Class<P> packetClass, PacketListener<P> listener);

    /**
     * Retrieves all listeners registered for the given packet type.
     *
     * @param packetClass the class of the packet type, not null
     * @param <P>         the packet type
     * @return a collection of listeners for the packet type, never null
     */
    <P extends Packet> Collection<PacketListener> getPacketListeners(Class<P> packetClass);
}