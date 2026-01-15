package io.github.milesreimann.packetsystem.api.packet;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
@Getter
@Setter
public abstract class Packet {
    private UUID uniqueId;
}