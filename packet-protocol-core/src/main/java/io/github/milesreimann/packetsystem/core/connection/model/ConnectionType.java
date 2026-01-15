package io.github.milesreimann.packetsystem.core.connection.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Getter
public enum ConnectionType {
    SERVER("Server"),
    CLIENT("Client");

    private final String displayName;
}
