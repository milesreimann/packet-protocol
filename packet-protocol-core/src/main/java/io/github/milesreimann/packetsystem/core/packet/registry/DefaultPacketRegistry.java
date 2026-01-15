package io.github.milesreimann.packetsystem.core.packet.registry;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.api.packet.registry.PacketRegistry;
import io.github.milesreimann.packetsystem.core.util.PacketIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
    private final PacketScanner packetScanner;

    private final Map<Class<? extends Packet>, Integer> classToIdMap = new ConcurrentHashMap<>();
    private final Map<Integer, Class<? extends Packet>> idToClassMap = new ConcurrentHashMap<>();

    @Override
    public <P extends Packet> void registerPacket(Class<P> packetClass) {
        if (classToIdMap.containsKey(packetClass)) {
            int existingId = classToIdMap.get(packetClass);
            log.warn("Packet '{}' is already registered with ID {} Skipping registration", packetClass.getName(), existingId);
            return;
        }

        log.info("Registering packet '{}'", packetClass.getName());
        int packetId = PacketIdGenerator.generatePacketId(packetClass.getName(), idToClassMap);

        registerPacketWithId(packetId, packetClass);
    }

    @Override
    public void registerPackets(String packageName) {
        log.debug("Registering packets from package '{}'", packageName);
        packetScanner.scanPackage(packageName).forEach(this::registerPacket);
    }

    @Override
    public void registerPacketsRecursive(String packageName) {
        log.debug("Registering packets from package '{}' recursively", packageName);
        packetScanner.scanPackageRecursive(packageName).forEach(this::registerPacket);
    }

    @Override
    public <P extends Packet> void unregisterPacket(Class<P> packetClass) {
        Integer packetId = classToIdMap.remove(packetClass);
        if (packetId == null) {
            log.warn("Attempted to unregister non-registered packet '{}'", packetClass.getName());
            return;
        }

        idToClassMap.remove(packetId);
        log.info("Unregistered packet '{}' with ID {}", packetClass.getName(), packetId);
    }

    @Override
    public Optional<Class<? extends Packet>> getPacketClassById(int packetId) {
        return Optional.ofNullable(idToClassMap.get(packetId));
    }

    public Class<? extends Packet> getPacketClassByIdOrThrow(int packetId) {
        return getPacketClassById(packetId)
            .orElseThrow(() -> new NoSuchElementException("Packet class of ID '" + packetId + "' was not found"));
    }

    @Override
    public <P extends Packet> Optional<Integer> getPacketIdByClass(Class<P> packetClass) {
        return Optional.ofNullable(classToIdMap.get(packetClass));
    }

    public <P extends Packet> Integer getPacketIdByClassOrThrow(Class<P> packetClass) {
        return getPacketIdByClass(packetClass).orElseThrow(() -> new NoSuchElementException("Packet ID of class '" + packetClass + "' was not found"));
    }

    private <P extends Packet> void registerPacketWithId(int packetId, Class<P> packetClass) {
        if (idToClassMap.containsKey(packetId)) {
            log.warn(
                "Packet ID collision detected: ID '{}' is already used by '{}' while registering '{}",
                packetId,
                idToClassMap.get(packetId),
                packetClass
            );
            return;
        }

        classToIdMap.put(packetClass, packetId);
        idToClassMap.put(packetId, packetClass);

        log.info("Successfully registered packet '{}' with ID {}", packetClass.getName(), packetId);
    }
}