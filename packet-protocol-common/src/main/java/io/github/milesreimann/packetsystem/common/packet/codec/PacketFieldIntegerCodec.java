package io.github.milesreimann.packetsystem.common.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;


/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketFieldIntegerCodec implements PacketFieldCodec<Integer> {
    @Override
    public void encode(Integer value, ByteBuf byteBuf) {
        boolean isNull = value == null;
        byteBuf.writeBoolean(isNull);

        if (!isNull) {
            byteBuf.writeInt(value);
        }
    }

    @Override
    public Integer decode(ByteBuf byteBuf) {
        if (byteBuf.readBoolean()) {
            return null;
        }

        return byteBuf.readInt();
    }
}