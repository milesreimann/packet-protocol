package io.github.deroq42.packetsystem.core.exception;

import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
public class ReflectionException extends RuntimeException {
    public ReflectionException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
