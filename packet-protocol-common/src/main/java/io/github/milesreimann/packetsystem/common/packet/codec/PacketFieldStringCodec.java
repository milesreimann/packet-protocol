package io.github.milesreimann.packetsystem.common.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;


import java.nio.charset.Charset;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
public abstract class PacketFieldStringCodec implements PacketFieldCodec<String> {
    private final Charset charset;

    @Override
    public void encode(String s, ByteBuf byteBuf) {
        if (s == null) {
            byteBuf.writeInt(0);
            return;
        }

        byte[] bytes = s.getBytes(charset);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    @Override
    public String decode(ByteBuf byteBuf) {
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