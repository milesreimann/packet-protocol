package io.github.deroq42.packetsystem.api.packet.listener;

import io.github.deroq42.packetsystem.api.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketListenerRegistry {
    <P extends Packet> void registerPacketListener(@NotNull PacketListener<P> listener);

    <P extends Packet> void unregisterPacketListener(
        @NotNull Class<P> packetClass,
        @NotNull PacketListener<P> listener
    );

    <P extends Packet> @NotNull Collection<PacketListener> getPacketListeners(@NotNull Class<P> packetClass);
}
