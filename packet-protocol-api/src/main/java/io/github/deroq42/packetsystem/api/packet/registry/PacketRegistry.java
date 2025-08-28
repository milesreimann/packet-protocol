package io.github.deroq42.packetsystem.api.packet.registry;

import io.github.deroq42.packetsystem.api.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketRegistry {
    <P extends Packet> void registerPacket(@NotNull Class<P> packetClass);

    void registerPackets(@NotNull String packageName);

    void registerPacketsRecursive(@NotNull String packageName);

    <P extends Packet> void unregisterPacket(@NotNull Class<P> packetClass);

    Optional<Class<? extends Packet>> getPacketClassById(int packetId);

    <P extends Packet> Optional<Integer> getPacketIdByClass(@NotNull Class<P> packetClass);
}