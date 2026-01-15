package io.github.milesreimann.packetsystem.api.connection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the global {@link ConnectionFactory} instance.
 * <p>
 * This class is used internally by {@link ConnectionFactory#getInstance()} to lazily load or provide a factory
 * implementation.
 * </p>
 *
 * <p>
 * The factory can only be set once. Any further attempt will result in an {@link IllegalStateException}.
 * </p>
 *
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionFactoryHolder {
    private static final AtomicReference<ConnectionFactory> INSTANCE = new AtomicReference<>();

    /**
     * Sets the global {@link ConnectionFactory} instance.
     * <p>
     * This method may only be called once. Further calls will throw an exception.
     * </p>
     *
     * @param factory the factory to set, not null
     * @throws IllegalStateException if a factory has already been set
     */
    public static void set(ConnectionFactory factory) {
        if (!INSTANCE.compareAndSet(null, factory)) {
            throw new IllegalStateException("ConnectionFactory already set");
        }
    }

    /**
     * Returns the global {@link ConnectionFactory} instance.
     * <p>
     * If no factory has been set manually, the implementation is loaded via {@link ServiceLoader}.
     * </p>
     *
     * @return the global connection factory, never {@code null}
     * @throws IllegalStateException if no implementation is found via {@link ServiceLoader}
     */
    public static ConnectionFactory get() {
        if (INSTANCE.get() != null) {
            return INSTANCE.get();
        }

        ServiceLoader<ConnectionFactory> loader = ServiceLoader.load(ConnectionFactory.class);
        ConnectionFactory factory = loader.findFirst()
            .orElseThrow(() -> new IllegalStateException("No ConnectionFactory implementation found"));
        ConnectionFactoryHolder.set(factory);

        return factory;
    }
}