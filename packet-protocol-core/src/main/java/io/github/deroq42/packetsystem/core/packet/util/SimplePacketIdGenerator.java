package io.github.deroq42.packetsystem.core.packet.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SimplePacketIdGenerator {
    private static final int FNV_PRIME = 0x01000193;
    private static final int FNV_OFFSET_BASIS = 0x811c9dc5;

    public static int generatePacketId(@NotNull String className, @NotNull Map<Integer, ?> usedIds) {
        int id = fnv1aHash(className);

        while (usedIds.containsKey(id)) {
            id++;
        }

        return id;
    }

    private static int fnv1aHash(String input) {
        int hash = FNV_OFFSET_BASIS;
        for (byte b : input.getBytes()) {
            hash ^= (b & 0xff);
            hash *= FNV_PRIME;
        }

        return Math.abs(hash);
    }
}
