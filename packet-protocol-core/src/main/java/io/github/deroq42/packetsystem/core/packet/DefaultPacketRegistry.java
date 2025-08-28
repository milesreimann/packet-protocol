package io.github.deroq42.packetsystem.core.packet;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.PacketRegistry;
import io.github.deroq42.packetsystem.core.packet.util.SimplePacketIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Log4j2
public class DefaultPacketRegistry implements PacketRegistry {
    private final @NotNull PacketScanner packetScanner;

    private final @NotNull Map<Class<? extends Packet>, Integer> packetClassToIdMap = new ConcurrentHashMap<>();
    private final @NotNull Map<Integer, Class<? extends Packet>> packetIdToClassMap = new ConcurrentHashMap<>();

    @Override
    public <P extends Packet> void registerPacket(@NotNull Class<P> packetClass) {
        if (packetClassToIdMap.containsKey(packetClass)) {
            int existingId = packetClassToIdMap.get(packetClass);
            log.warn("Packet '{}' is already registered with ID {}. Skipping registration", packetClass.getName(), existingId);
            return;
        }

        log.info("Registering packet '{}'", packetClass.getName());
        int packetId = SimplePacketIdGenerator.generatePacketId(packetClass.getSimpleName(), packetIdToClassMap);

        addPacketToRegistry(packetId, packetClass);
    }

    @Override
    public void registerPackets(@NotNull String packageName) {
        log.debug("Registering packets from package '{}'", packageName);
        packetScanner.scanPackage(packageName);
    }

    @Override
    public void registerPacketsRecursive(@NotNull String packageName) {
        log.debug("Registering packets from package '{}' recursively", packageName);
        packetScanner.scanPackageRecursive(packageName);
    }

    @Override
    public <P extends Packet> void unregisterPacket(@NotNull Class<P> packetClass) {
        Integer packetId = packetClassToIdMap.remove(packetClass);
        if (packetId == null) {
            log.warn("Attempted to unregister non-registered packet '{}'", packetClass.getName());
            return;
        }

        packetIdToClassMap.remove(packetId);
        log.info("Unregistered packet '{}' with ID {}.", packetClass.getName(), packetId);
    }

    @Override
    public Optional<Class<? extends Packet>> getPacketClassById(int packetId) {
        return Optional.ofNullable(packetIdToClassMap.get(packetId));
    }

    public @NotNull Class<? extends Packet> getPacketClassByIdOrThrow(int packetId) {
        return getPacketClassById(packetId).orElseThrow(() -> new NoSuchElementException("Packet class of ID '" + packetId + "' was not found"));
    }

    @Override
    public <P extends Packet> Optional<Integer> getPacketIdByClass(@NotNull Class<P> packetClass) {
        return Optional.ofNullable(packetClassToIdMap.get(packetClass));
    }

    public @NotNull <P extends Packet> Integer getPacketIdByClassOrThrow(@NotNull Class<P> packetClass) {
        return getPacketIdByClass(packetClass).orElseThrow(() -> new NoSuchElementException("Packet ID of class '" + packetClass + "' was not found"));
    }

    private <P extends Packet> void addPacketToRegistry(int packetId, @NotNull Class<P> packetClass) {
        if (packetIdToClassMap.containsKey(packetId)) {
            logCollisionDetected(packetId, packetClass);
            return;
        }

        packetClassToIdMap.put(packetClass, packetId);
        packetIdToClassMap.put(packetId, packetClass);

        log.info("Successfully registered packet '{}' with ID {}", packetClass.getName(), packetId);
    }

    private void logCollisionDetected(int packetId, @NotNull Class<?> packetClass) {
        log.warn(
            "Packet ID collision detected: ID '{}' is already used by '{}' while registering '{}",
            packetId,
            packetIdToClassMap.get(packetId),
            packetClass
        );
    }
}