package io.github.deroq42.packetsystem.api.packet.listener;

import io.github.deroq42.packetsystem.api.connection.Connection;
import io.github.deroq42.packetsystem.api.packet.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
@FunctionalInterface
public interface PacketListener<P extends Packet> {
    void handle(@NotNull P packet, @NotNull Connection connection);
}
