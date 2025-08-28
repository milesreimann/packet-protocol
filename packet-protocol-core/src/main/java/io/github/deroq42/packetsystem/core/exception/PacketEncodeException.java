package io.github.deroq42.packetsystem.core.exception;

import io.github.deroq42.packetsystem.api.packet.Packet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketEncodeException extends RuntimeException {
    public PacketEncodeException(@NotNull Packet packet, @NotNull Exception e) {
        super("Failed to encode packet '" + packet.getUniqueId() + "'", e);
    }
}