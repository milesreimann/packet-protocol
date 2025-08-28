package io.github.deroq42.packetsystem.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketInstantiationException extends RuntimeException {
    public PacketInstantiationException(@NotNull String message, @NotNull Exception e) {
        super(message, e);
    }
}