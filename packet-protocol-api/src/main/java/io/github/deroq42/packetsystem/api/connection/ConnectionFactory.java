package io.github.deroq42.packetsystem.api.connection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Factory for creating {@link Connection} instances.
 * <p>
 * Provides methods to create both client and server connections,
 * with optional callbacks for connection and disconnection events.
 * </p>
 *
 * <p>
 * Use {@link #getInstance()} to get the default implementation.
 * </p>
 *
 * @author Miles
 * @since 28.08.25
 */
public interface ConnectionFactory {

    /**
     * Creates a new server-side connection.
     *
     * @param host               the host address the server will bind to, not null
     * @param port               the port the server will listen on
     * @param connectCallback    optional callback invoked when a client connects
     * @param disconnectCallback optional callback invoked when a client disconnects
     * @return a new server connection, never {@code null}
     */
    @NotNull Connection createServerConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    );

    /**
     * Creates a new server-side connection without callbacks.
     *
     * @param host the host address the server will bind to, not null
     * @param port the port the server will listen on
     * @return a new server connection, never {@code null}
     */
    @NotNull Connection createServerConnection(@NotNull String host, int port);

    /**
     * Creates a new client-side connection.
     *
     * @param host               the remote host to connect to, not null
     * @param port               the remote port
     * @param connectCallback    optional callback invoked when the connection is established
     * @param disconnectCallback optional callback invoked when the connection is closed
     * @return a new client connection, never {@code null}
     */
    @NotNull Connection createClientConnection(
        @NotNull String host,
        int port,
        @Nullable Consumer<Void> connectCallback,
        @Nullable Consumer<Void> disconnectCallback
    );

    /**
     * Creates a new client-side connection without callbacks.
     *
     * @param host the remote host to connect to, not null
     * @param port the remote port
     * @return a new client connection, never {@code null}
     */
    @NotNull Connection createClientConnection(@NotNull String host, int port);

    /**
     * Provides the default {@link ConnectionFactory} implementation.
     *
     * @return the singleton connection factory.
     */
    static @Nullable ConnectionFactory getInstance() {
        return ConnectionFactoryHolder.get();
    }
}