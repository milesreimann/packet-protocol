package io.github.deroq42.packetsystem.core.packet.backlog;

import io.github.deroq42.packetsystem.api.connection.Connection;
import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.core.packet.backlog.model.FlushResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
@RequiredArgsConstructor
public class PacketBacklog {
    private final @NotNull Connection connection;

    private final @NotNull ConcurrentLinkedQueue<Packet> backlog = new ConcurrentLinkedQueue<>();
    private final @NotNull AtomicBoolean flushInProgress = new AtomicBoolean(false);

    public boolean add(@NotNull Packet packet) {
        log.debug("Adding packet '{}' to backlog", packet.getUniqueId());

        if (flushInProgress.get()) {
            log.warn(
                "Adding packet '{}' to backlog while flush is in progress. Packet will be queued for next flush.",
                packet.getUniqueId()
            );
        }

        boolean added = backlog.offer(packet);
        if (!added) {
            log.error("Failed to add packet '{}' to backlog queue", packet.getUniqueId());
            return false;
        }

        log.info("Added packet '{}' to backlog. Current backlog size: {}", packet.getUniqueId(), backlog.size());
        return true;
    }

    public @NotNull FlushResult flush() {
        log.debug("Starting backlog flush operation");

        if (!flushInProgress.compareAndSet(false, true)) {
            log.warn("Flush operation already in progress");
            return new FlushResult(0, 0, true);
        }

        try {
            if (backlog.isEmpty()) {
                log.debug("Backlog is empty, nothing to flush");
                return new FlushResult(0, 0, false);
            }

            List<Packet> packetsToFlush = drainBacklog();
            int packetCount = packetsToFlush.size();
            log.info("Flushing {} packet(s) from backlog", packetCount);

            return flushPackets(packetsToFlush);
        } finally {
            flushInProgress.set(false);
        }
    }

    public boolean clear() {
        if (!flushInProgress.compareAndSet(false, true)) {
            log.warn("Cannot clear backlog because flush operation is already in progress");
            return false;
        }

        try {
            int clearedCount = backlog.size();
            backlog.clear();
            log.info("Backlog cleared ({} packets removed)", clearedCount);

            return true;
        } finally {
            flushInProgress.set(false);
        }
    }

    private @NotNull List<Packet> drainBacklog() {
        List<Packet> packetsToFlush = new ArrayList<>();
        Packet packet;

        while ((packet = backlog.poll()) != null) {
            packetsToFlush.add(packet);
        }

        return packetsToFlush;
    }

    private @NotNull FlushResult flushPackets(@NotNull List<Packet> packets) {
        int successfulFlushes = 0;
        int failedFlushes = 0;
        List<Packet> failedPackets = new ArrayList<>();

        for (Packet packet : packets) {
            try {
                log.trace("Flushing packet '{}'", packet.getUniqueId());
                connection.sendPacket(packet);
                successfulFlushes++;
            } catch (Exception e) {
                log.error("Failed to flush packet '{}'. Will be re-added to backlog", packet.getUniqueId(), e);
                failedPackets.add(packet);
                failedFlushes++;
            }
        }

        failedPackets.forEach(backlog::offer);

        log.info("Backlog flush completed: {} successful, {} failed", successfulFlushes, failedFlushes);
        return new FlushResult(successfulFlushes, failedFlushes, false);
    }
}