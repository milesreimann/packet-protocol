package io.github.milesreimann.packetsystem.core.connection;

import io.github.milesreimann.packetsystem.api.connection.Connection;
import io.github.milesreimann.packetsystem.api.connection.ConnectionFactory;


import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
public class DefaultConnectionFactory implements ConnectionFactory {
    @Override
    public Connection createServerConnection(
        String host,
        int port,
        Consumer<Void> connectCallback,
        Consumer<Void> disconnectCallback
    ) {
        return new ServerConnection(host, port, connectCallback, disconnectCallback);
    }

    @Override
    public Connection createServerConnection(String host, int port) {
        return new ServerConnection(host, port);
    }

    @Override
    public Connection createClientConnection(
        String host,
        int port,
        Consumer<Void> connectCallback,
        Consumer<Void> disconnectCallback
    ) {
        return new ClientConnection(host, port, connectCallback, disconnectCallback);
    }

    @Override
    public Connection createClientConnection(String host, int port) {
        return new ClientConnection(host, port);
    }
}