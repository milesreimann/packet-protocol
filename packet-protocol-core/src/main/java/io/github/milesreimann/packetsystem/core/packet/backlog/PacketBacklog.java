package io.github.milesreimann.packetsystem.core.packet.backlog;

import io.github.milesreimann.packetsystem.api.connection.Connection;
import io.github.milesreimann.packetsystem.api.packet.Packet;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
@RequiredArgsConstructor
public class PacketBacklog {
    private final Connection connection;

    private final Queue<Packet> backlog = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean flushInProgress = new AtomicBoolean(false);

    public void add(Packet packet) {
        log.debug("Adding packet '{}' to backlog", packet.getUniqueId());

        if (flushInProgress.get()) {
            log.warn(
                "Adding packet '{}' to backlog while flush is in progress. Packet will be queued for next flush.",
                packet.getUniqueId()
            );
        }

        boolean added = backlog.offer(packet);
        if (!added) {
            log.warn("Failed to add packet '{}' to backlog queue", packet.getUniqueId());
            return;
        }

        log.info("Added packet '{}' to backlog. Current backlog size: {}", packet.getUniqueId(), backlog.size());
    }

    public void flush() {
        log.debug("Starting backlog flush operation");

        if (!flushInProgress.compareAndSet(false, true)) {
            log.warn("Flush operation already in progress");
            return;
        }

        try {
            performFlush();
        } finally {
            flushInProgress.set(false);
        }
    }

    public void clear() {
        if (!flushInProgress.compareAndSet(false, true)) {
            log.warn("Cannot clear backlog because flush operation is already in progress");
            return;
        }

        try {
            int clearedCount = backlog.size();
            backlog.clear();
            log.info("Backlog cleared ({} packets removed)", clearedCount);
        } finally {
            flushInProgress.set(false);
        }
    }

    private void performFlush() {
        if (backlog.isEmpty()) {
            log.debug("Backlog is empty, nothing to flush");
            return;
        }

        List<Packet> packetsToFlush = drainBacklog();
        log.info("Flushing {} packet(s) from backlog", packetsToFlush.size());

        sendPackets(packetsToFlush);
    }

    private void sendPackets(List<Packet> packets) {
        int successCount = 0;
        int failureCount = 0;
        List<Packet> failedPackets = new ArrayList<>();

        for (Packet packet : packets) {
            if (!attemptSend(packet)) {
                failedPackets.add(packet);
                failureCount++;
            } else {
                successCount++;
            }
        }

        if (!failedPackets.isEmpty()) {
            failedPackets.forEach(backlog::offer);
            log.debug("Re-added {} failed packets to backlog", failedPackets.size());
        }

        log.info(
            "Backlog flush completed: {} successful, {} failed",
            successCount,
            failureCount
        );
    }

    private boolean attemptSend(Packet packet) {
        try {
            log.trace("Flushing packet '{}'", packet.getUniqueId());
            connection.sendPacket(packet);
            return true;
        } catch (Exception e) {
            log.error(
                "Failed to flush packet '{}'. Will be re-added to backlog",
                packet.getUniqueId(),
                e
            );
            return false;
        }
    }

    private List<Packet> drainBacklog() {
        List<Packet> packetsToFlush = new ArrayList<>();
        Packet packet;

        while ((packet = backlog.poll()) != null) {
            packetsToFlush.add(packet);
        }

        return packetsToFlush;
    }
}