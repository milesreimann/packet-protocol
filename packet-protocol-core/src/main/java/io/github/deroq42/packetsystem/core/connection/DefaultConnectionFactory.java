package io.github.deroq42.packetsystem.core.connection;

import io.github.deroq42.packetsystem.api.connection.Connection;
import io.github.deroq42.packetsystem.api.connection.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
public class DefaultConnectionFactory implements ConnectionFactory {
    @Override
    public @NotNull Connection createServerConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    ) {
        return new ServerConnection(host, port, connectCallback, disconnectCallback);
    }

    @Override
    public @NotNull Connection createServerConnection(@NotNull String host, int port) {
        return new ServerConnection(host, port);
    }

    @Override
    public @NotNull Connection createClientConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    ) {
        return new ClientConnection(host, port, connectCallback, disconnectCallback);
    }

    @Override
    public @NotNull Connection createClientConnection(@NotNull String host, int port) {
        return new ClientConnection(host, port);
    }
}