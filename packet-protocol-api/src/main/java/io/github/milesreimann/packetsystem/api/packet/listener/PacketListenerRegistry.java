package io.github.milesreimann.packetsystem.api.packet.listener;

import io.github.milesreimann.packetsystem.api.packet.Packet;

import java.util.Collection;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketListenerRegistry {
    <P extends Packet> void registerPacketListener(PacketListener<P> listener);

    <P extends Packet> void unregisterPacketListener(
        Class<P> packetClass,
        PacketListener<P> listener
    );

    <P extends Packet> Collection<PacketListener> getPacketListeners(Class<P> packetClass);
}
