package io.github.deroq42.packetsystem.api.packet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
public abstract class Packet {
    private @Nullable UUID uniqueId;

    public @Nullable UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(@NotNull UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
}
