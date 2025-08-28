package io.github.deroq42.packetsystem.api.packet.codec;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketFieldCodec<T> {
    void encode(@Nullable T value, @NotNull ByteBuf byteBuf);

    @Nullable T decode(@NotNull ByteBuf byteBuf);
}
