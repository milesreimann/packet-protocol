package io.github.deroq42.packetsystem.core.packet.codec.model;

import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * @author Miles
 * @since 28.08.25
 */
public record PacketFieldInfo(
    @NotNull Field field,
    @NotNull Class<? extends PacketFieldCodec> codecClass
) {
}
