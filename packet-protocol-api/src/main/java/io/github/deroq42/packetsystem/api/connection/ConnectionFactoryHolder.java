package io.github.deroq42.packetsystem.api.connection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionFactoryHolder {
    private static final AtomicReference<ConnectionFactory> INSTANCE = new AtomicReference<>();

    public static void set(@NotNull ConnectionFactory factory) {
        if (!INSTANCE.compareAndSet(null, factory)) {
            throw new IllegalStateException("ConnectionFactory already set");
        }
    }

    public static @NotNull ConnectionFactory get() {
        if (INSTANCE.get() != null) {
            return INSTANCE.get();
        }

        ServiceLoader<ConnectionFactory> loader = ServiceLoader.load(ConnectionFactory.class);
        ConnectionFactory factory = loader.findFirst().orElseThrow(() -> new IllegalStateException("No ConnectionFactory implementation found"));
        ConnectionFactoryHolder.set(factory);

        return factory;
    }
}