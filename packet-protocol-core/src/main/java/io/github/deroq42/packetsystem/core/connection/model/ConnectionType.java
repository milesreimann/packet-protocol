package io.github.deroq42.packetsystem.core.connection.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Getter
public enum ConnectionType {
    SERVER("Server"),
    CLIENT("Client");

    private final @NotNull String displayName;
}
