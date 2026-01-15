package io.github.milesreimann.packetsystem.api.packet.registry;

import io.github.milesreimann.packetsystem.api.packet.Packet;

import java.util.Optional;

/**
 * Registry for managing packet classes and their unique IDs.
 * <p>
 * Used by connections and codecs to map packet classes to IDs
 * for serialization and deserialization.
 * </p>
 *
 * <p>
 * Implementations should support thread-safe registration and lookups.
 * </p>
 *
 * @author Miles
 * @since 28.08.25
 */
public interface PacketRegistry {
    /**
     * Registers a single packet class.
     *
     * @param packetClass the packet class to register, not null
     * @param <P>         the type of the packet
     */
    <P extends Packet> void registerPacket(Class<P> packetClass);

    /**
     * Registers all packet classes found in the specified package.
     *
     * @param packageName the package name to scan, not null
     */
    void registerPackets(String packageName);

    /**
     * Registers all packet classes found in the specified package and its subpackages.
     *
     * @param packageName the package name to scan recursively, not null
     */
    void registerPacketsRecursive(String packageName);

    /**
     * Unregisters a previously registered packet class.
     *
     * @param packetClass the packet class to unregister, not null
     * @param <P>         the type of the packet
     */
    <P extends Packet> void unregisterPacket(Class<P> packetClass);

    /**
     * Retrieves the packet class associated with the given packet ID.
     *
     * @param packetId the ID of the packet
     * @return an Optional containing the packet class, or empty if not found
     */
    Optional<Class<? extends Packet>> getPacketClassById(int packetId);

    /**
     * Retrieves the packet ID associated with the given packet class.
     *
     * @param packetClass the packet class, not null
     * @param <P>         the type of the packet
     * @return an Optional containing the packet ID, or empty if not registered
     */
    <P extends Packet> Optional<Integer> getPacketIdByClass(Class<P> packetClass);
}
