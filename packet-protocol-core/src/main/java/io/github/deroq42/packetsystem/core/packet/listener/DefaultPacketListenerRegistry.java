package io.github.deroq42.packetsystem.core.packet.listener;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.listener.PacketListener;
import io.github.deroq42.packetsystem.api.packet.listener.PacketListenerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
@RequiredArgsConstructor
public class DefaultPacketListenerRegistry implements PacketListenerRegistry {
    @SuppressWarnings("rawtypes")
    private final @NotNull Map<Class<? extends Packet>, Collection<PacketListener>> packetClassToListenersMap = new ConcurrentHashMap<>();

    @Override
    public <P extends Packet> void registerPacketListener(@NotNull PacketListener<P> listener) {
        determinePacketClassFromListener(listener.getClass()).ifPresentOrElse(
            packetClass -> addPacketListenerToRegistry(packetClass, listener),
            () -> log.warn(
                "Failed to register packet listener '{}': Unable to determine packet class from generic type",
                listener.getClass().getSimpleName()
            )
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P extends Packet> void unregisterPacketListener(
        @NotNull Class<P> packetClass,
        @NotNull PacketListener<P> listener
    ) {
        Collection<PacketListener<P>> listeners = (Collection<PacketListener<P>>) (Collection<?>) packetClassToListenersMap.get(packetClass);
        if (listeners == null) {
            return;
        }

        if (!listeners.remove(listener)) {
            log.warn(
                "Packet listener '{}' was not registered for packet class '{}'. Nothing to unregister",
                listener.getClass().getName(),
                packetClass.getName()
            );

            return;
        }

        log.info("Unregistered packet listener: {}", listener.getClass().getName());

        if (listeners.isEmpty()) {
            packetClassToListenersMap.remove(packetClass);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull <P extends Packet> Collection<PacketListener> getPacketListeners(@NotNull Class<P> packetClass) {
        Collection<PacketListener> listeners = packetClassToListenersMap.get(packetClass);
        if (listeners == null || listeners.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableCollection(listeners);
    }

    @SuppressWarnings("rawtypes")
    private <P extends Packet> void addPacketListenerToRegistry(
        @NotNull Class<P> packetClass,
        @NotNull PacketListener listener
    ) {
        packetClassToListenersMap
            .computeIfAbsent(packetClass, _ -> new CopyOnWriteArrayList<>())
            .add(listener);

        log.info("Registered packet listener: {}", listener.getClass());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <P extends Packet> Optional<Class<P>> determinePacketClassFromListener(@NotNull Class<? extends PacketListener> listenerClass) {
        log.debug("Determining packet class from listener class '{}'", listenerClass.getName());

        for (Type type : listenerClass.getGenericInterfaces()) {
            if (!(type instanceof ParameterizedType parameterizedType)) {
                log.trace("Skipping non-parameterized interface type: {}", type);
                continue;
            }

            Type rawType = parameterizedType.getRawType();
            if (!PacketListener.class.equals(rawType)) {
                log.trace("Skipping interface that is not PacketListener: {}", rawType);
                continue;
            }

            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length == 0) {
                log.debug("PacketListener interface has no type arguments in class '{}'", listenerClass.getName());
                continue;
            }

            Type packetType = typeArguments[0];
            if (!(packetType instanceof Class<?> packetClass)) {
                log.debug("First type argument is not a class in listener '{}'", listenerClass.getName());
                continue;
            }

            if (!Packet.class.isAssignableFrom(packetClass)) {
                log.debug(
                    "Type argument '{}' does not implement Packet interface in listener '{}'",
                    packetClass.getName(),
                    listenerClass.getName()
                );

                continue;
            }

            return Optional.of((Class<P>) packetClass);
        }

        return Optional.empty();
    }
}