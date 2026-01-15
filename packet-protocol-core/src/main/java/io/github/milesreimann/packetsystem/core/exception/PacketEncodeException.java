package io.github.milesreimann.packetsystem.core.exception;

import io.github.milesreimann.packetsystem.api.packet.Packet;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketEncodeException extends RuntimeException {
    public PacketEncodeException(Packet packet, Exception e) {
        super("Failed to encode packet '" + packet.getUniqueId() + "'", e);
    }

    public PacketEncodeException(Packet packet, String message) {
        super("Failed to encode packet '" + packet.getUniqueId() + "': " + message);
    }
}