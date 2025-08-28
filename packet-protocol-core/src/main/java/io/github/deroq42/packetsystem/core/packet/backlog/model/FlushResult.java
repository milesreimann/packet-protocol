package io.github.deroq42.packetsystem.core.packet.backlog.model;

/**
 * @author Miles
 * @since 28.08.25
 */
public record FlushResult(int successfulFlushes, int failedFlushed, boolean skipped) {
}
