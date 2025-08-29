package io.github.deroq42.packetsystem.api.connection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the global {@link ConnectionFactory} instance.
 * <p>
 * This class is used internally by {@link ConnectionFactory#getInstance()}
 * to lazily load or provide a factory implementation.
 * </p>
 *
 * <h3>Usage</h3>
 * <ul>
 *   <li>By default, the factory is loaded via {@link ServiceLoader} when first accessed.</li>
 *   <li>A custom implementation may be set programmatically using {@link #set(ConnectionFactory)}.
 *       This must be done before the first call to {@link #get()}.</li>
 * </ul>
 *
 * <p>
 * The factory can only be set once. Any further attempt will result in
 * an {@link IllegalStateException}.
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
    public static void set(@NotNull ConnectionFactory factory) {
        if (!INSTANCE.compareAndSet(null, factory)) {
            throw new IllegalStateException("ConnectionFactory already set");
        }
    }

    /**
     * Returns the global {@link ConnectionFactory} instance.
     * <p>
     * If no factory has been set manually, the implementation is loaded
     * via {@link ServiceLoader}.
     * </p>
     *
     * @return the global connection factory, never {@code null}
     * @throws IllegalStateException if no implementation is found via {@link ServiceLoader}
     */
    public static @NotNull ConnectionFactory get() {
        if (INSTANCE.get() != null) {
            return INSTANCE.get();
        }

        ServiceLoader<ConnectionFactory> loader = ServiceLoader.load(ConnectionFactory.class);
        ConnectionFactory factory = loader.findFirst().orElseThrow(() ->
            new IllegalStateException("No ConnectionFactory implementation found"));
        ConnectionFactoryHolder.set(factory);

        return factory;
    }
}
