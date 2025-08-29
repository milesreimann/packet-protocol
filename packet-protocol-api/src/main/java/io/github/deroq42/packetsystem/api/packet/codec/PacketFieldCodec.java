package io.github.deroq42.packetsystem.api.packet.codec;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Codec for encoding and decoding a single packet field of type {@code T}.
 * <p>
 * Implementations define how a field is serialized to and deserialized from a {@link ByteBuf}.
 * </p>
 *
 * @param <T> the type of the field handled by this codec
 * @author Miles
 * @since 28.08.25
 */
public interface PacketFieldCodec<T> {
    /**
     * Encodes the given field value into the provided {@link ByteBuf}.
     *
     * @param value   the value to encode; may be {@code null} if the field allows it
     * @param byteBuf the buffer to write the encoded data into, not null
     */
    void encode(@Nullable T value, @NotNull ByteBuf byteBuf);

    /**
     * Decodes a field value from the provided {@link ByteBuf}.
     *
     * @param byteBuf the buffer to read data from, not null
     * @return the decoded field value, may be {@code null} if the field allows it
     */
    @Nullable T decode(@NotNull ByteBuf byteBuf);
}
