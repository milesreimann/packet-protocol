package io.github.milesreimann.packetsystem.common.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;


import java.util.UUID;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketFieldUUIDCodec implements PacketFieldCodec<UUID> {
    @Override
    public void encode(UUID value, ByteBuf byteBuf) {
        if (value == null) {
            byteBuf.writeLong(0);
            return;
        }

        byteBuf.writeLong(value.getMostSignificantBits());
        byteBuf.writeLong(value.getLeastSignificantBits());
    }

    @Override
    public UUID decode(ByteBuf byteBuf) {
        long mostSigBits = byteBuf.readLong();
        if (mostSigBits == 0) {
            return null;
        }

        return new UUID(mostSigBits, byteBuf.readLong());
    }
}