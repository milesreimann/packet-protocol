package io.github.deroq42.packetsystem.api.packet.codec;

import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketCodec {
    <T> void addFieldCodec(@NotNull PacketFieldCodec<T> fieldCodec);
}
