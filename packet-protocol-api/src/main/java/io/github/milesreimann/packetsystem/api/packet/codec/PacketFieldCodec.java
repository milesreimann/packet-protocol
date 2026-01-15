package io.github.milesreimann.packetsystem.api.packet.codec;

import io.netty.buffer.ByteBuf;


/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketFieldCodec<T> {
    void encode(T value, ByteBuf byteBuf);

    T decode(ByteBuf byteBuf);
}
