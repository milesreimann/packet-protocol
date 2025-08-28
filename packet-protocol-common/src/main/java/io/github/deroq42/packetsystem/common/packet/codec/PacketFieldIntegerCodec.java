package io.github.deroq42.packetsystem.common.packet.codec;

import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketFieldIntegerCodec implements PacketFieldCodec<Integer> {
    @Override
    public void encode(@Nullable Integer value, @NotNull ByteBuf byteBuf) {
        boolean isNull = value == null;
        byteBuf.writeBoolean(isNull);

        if (!isNull) {
            byteBuf.writeInt(value);
        }
    }

    @Override
    public @Nullable Integer decode(@NotNull ByteBuf byteBuf) {
        if (byteBuf.readBoolean()) {
            return null;
        }

        return byteBuf.readInt();
    }
}