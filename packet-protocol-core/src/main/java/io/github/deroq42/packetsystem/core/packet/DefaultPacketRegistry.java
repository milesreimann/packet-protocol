package io.github.deroq42.packetsystem.core.packet;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.PacketRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
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

        log.debug("Starting registration process for packet '{}'", packetClass.getName());
        int packetId = determinePacketId(packetClass);

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
    public Optional<Class<? extends Packet>> getPacketClassById(int id) {
        return Optional.ofNullable(packetIdToClassMap.get(id));
    }

    @Override
    public <P extends Packet> Optional<Integer> getPacketIdByClass(@NotNull Class<P> packetClass) {
        return Optional.ofNullable(packetClassToIdMap.get(packetClass));
    }

    private <P extends Packet> void addPacketToRegistry(int packetId, @NotNull Class<P> packetClass) {
        if (packetIdToClassMap.containsKey(packetId)) {
            logCollisionDetected(packetId, packetClass);
            return;
        }

        packetClassToIdMap.put(packetClass, packetId);
        packetIdToClassMap.put(packetId, packetClass);

        log.info("Registered packet '{}' with ID {}", packetClass.getName(), packetId);
    }

    private @NotNull Integer determinePacketId(@NotNull Class<? extends Packet> packetClass) {
        log.debug("Determining packet ID for '{}'", packetClass.getName());

        return PacketIdReader.read(packetClass).orElseGet(() -> {
            log.error("Failed to read packet ID from class '{}'. Using hashCode of the packets class name as ID", packetClass.getName());
            return packetClass.getSimpleName().hashCode();
        });
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