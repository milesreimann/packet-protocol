package io.github.deroq42.packetsystem.common.packet.codec;

import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketFieldByteArrayCodec implements PacketFieldCodec<byte[]> {
    @Override
    public void encode(byte @Nullable [] value, @NotNull ByteBuf byteBuf) {
        boolean isNull = value == null;
        byteBuf.writeBoolean(isNull);

        if (isNull) {
            return;
        }

        int length = value.length;
        byteBuf.writeInt(length);

        if (length > 0) {
            byteBuf.writeBytes(value);
        }
    }

    @Override
    public byte @Nullable [] decode(@NotNull ByteBuf byteBuf) {
        if (byteBuf.readBoolean()) {
            return null;
        }

        int length = byteBuf.readInt();
        if (length == 0 || byteBuf.readableBytes() < length) {
            return new byte[0];
        }

        byte[] data = new byte[length];
        byteBuf.readBytes(data);

        return data;
    }
}