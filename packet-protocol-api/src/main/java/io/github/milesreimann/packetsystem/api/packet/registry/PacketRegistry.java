package io.github.milesreimann.packetsystem.api.packet.registry;

import io.github.milesreimann.packetsystem.api.packet.Packet;

import java.util.Optional;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketRegistry {
    <P extends Packet> void registerPacket(Class<P> packetClass);

    void registerPackets(String packageName);

    void registerPacketsRecursive(String packageName);

    <P extends Packet> void unregisterPacket(Class<P> packetClass);

    Optional<Class<? extends Packet>> getPacketClassById(int packetId);

    <P extends Packet> Optional<Integer> getPacketIdByClass(Class<P> packetClass);
}