package io.github.deroq42.packetsystem.common.packet.codec;

import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
public abstract class PacketFieldStringCodec implements PacketFieldCodec<String> {
    private final @NotNull Charset charset;

    @Override
    public void encode(@Nullable String s, @NotNull ByteBuf byteBuf) {
        if (s == null) {
            byteBuf.writeInt(0);
            return;
        }

        byte[] bytes = s.getBytes(charset);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public @Nullable String decode(@NotNull ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        if (length == 0) {
            return null;
        }

        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = byteBuf.readByte();
        }

        return new String(bytes, charset);
    }
}