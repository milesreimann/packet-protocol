package io.github.deroq42.packetsystem.common.packet.codec;

import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketFieldUUIDCodec implements PacketFieldCodec<UUID> {
    @Override
    public void encode(@Nullable UUID value, @NotNull ByteBuf byteBuf) {
        if (value == null) {
            byteBuf.writeLong(0);
            return;
        }

        byteBuf.writeLong(value.getMostSignificantBits());
        byteBuf.writeLong(value.getLeastSignificantBits());
    }

    @Override
    public @Nullable UUID decode(@NotNull ByteBuf byteBuf) {
        long mostSigBits = byteBuf.readLong();
        if (mostSigBits == 0) {
            return null;
        }

        return new UUID(mostSigBits, byteBuf.readLong());
    }
}