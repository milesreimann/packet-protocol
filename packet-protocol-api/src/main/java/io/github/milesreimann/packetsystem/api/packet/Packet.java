package io.github.milesreimann.packetsystem.api.packet;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base type for all packets transmitted over a connection.
 * <p>
 * Packets represent the fundamental data units exchanged between client and server.
 * Concrete packet types should extend this class and define additional fields
 * required by the protocol.
 * </p>
 *
 * <p>
 * Each packet is assigned a {@link #getUniqueId() uniqueId} during the
 * encoding/decoding process. While technically nullable during construction,
 * the ID should be considered mandatory and will always be set once the
 * packet is fully processed.
 * </p>
 *
 * @author Miles
 * @since 28.08.25
 */
@Getter
@Setter
public abstract class Packet {
    /**
     * The unique identifier of this packet.
     * <p>
     * Initially {@code null}, but guaranteed to be assigned when the
     * packet has been encoded or decoded.
     * </p>
     */
    private UUID uniqueId;
}