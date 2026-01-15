package io.github.milesreimann.packetsystem.api.connection;



import java.util.function.Consumer;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface ConnectionFactory {
    Connection createServerConnection(
        String host,
        int port,
        Consumer<Void> connectCallback,
        Consumer<Void> disconnectCallback
    );

    Connection createServerConnection(String host, int port);

    Connection createClientConnection(
        String host,
        int port,
        Consumer<Void> connectCallback,
        Consumer<Void> disconnectCallback
    );

    Connection createClientConnection(String host, int port);

    static ConnectionFactory getInstance() {
        return ConnectionFactoryHolder.get();
    }
}