package io.github.milesreimann.packetsystem.core.packet.codec.model;


import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
public record PacketHeader(int packetId, UUID uniqueId) {
}
