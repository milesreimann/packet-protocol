package io.github.deroq42.packetsystem.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketDecodeException  extends RuntimeException {
    public PacketDecodeException( @NotNull Exception e) {
        super("Failed to decode packet", e);
    }

    public PacketDecodeException(@NotNull String message) {
        super("Failed to decode packet: " + message);
    }
}
