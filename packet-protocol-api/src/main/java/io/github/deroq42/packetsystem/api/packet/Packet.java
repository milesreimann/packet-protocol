package io.github.deroq42.packetsystem.api.packet;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
@Getter
@Setter
public abstract class Packet {
    private @Nullable UUID uniqueId;
}
