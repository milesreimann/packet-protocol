package io.github.deroq42.packetsystem.core.packet.util;

import io.github.deroq42.packetsystem.api.packet.Packet;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * @author Miles
 * @since 28.08.25
 */
@Getter
public class ConstructorKey {
    private final @NotNull Class<? extends Packet> packetClass;
    private final @NotNull Class<?>[] paramTypes;
    private final int hashCode;

    public ConstructorKey(@NotNull Class<? extends Packet> packetClass, Object @NotNull [] params) {
        this.packetClass = packetClass;
        this.paramTypes = new Class<?>[params.length];

        for (int i = 0; i < params.length; i++) {
            this.paramTypes[i] = params[i].getClass();
        }

        this.hashCode = calculateHashCode();
    }

    private int calculateHashCode() {
        int result = packetClass.hashCode();
        result = 31 * result + Arrays.hashCode(paramTypes);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ConstructorKey that = (ConstructorKey) obj;
        return packetClass.equals(that.packetClass) && Arrays.equals(paramTypes, that.paramTypes);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}