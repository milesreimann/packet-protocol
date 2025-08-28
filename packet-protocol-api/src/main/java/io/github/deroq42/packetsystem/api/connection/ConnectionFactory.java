package io.github.deroq42.packetsystem.api.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface ConnectionFactory {
    @NotNull Connection createServerConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    );

    @NotNull Connection createServerConnection(@NotNull String host, int port);

    @NotNull Connection createClientConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    );

    @NotNull Connection createClientConnection(@NotNull String host, int port);

    static @NotNull ConnectionFactory getInstance() {
        return ConnectionFactoryHolder.get();
    }
}