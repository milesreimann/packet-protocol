package io.github.deroq42.packetsystem.core.packet.codec.model;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
public record PacketHeader(int packetId, @NotNull UUID uniqueId) {
}
