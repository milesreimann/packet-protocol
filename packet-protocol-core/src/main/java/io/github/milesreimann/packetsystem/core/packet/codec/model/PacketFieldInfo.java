package io.github.milesreimann.packetsystem.core.packet.codec.model;

import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;

import java.lang.reflect.Field;

/**
 * @author Miles
 * @since 28.08.25
 */
public record PacketFieldInfo(
    Field field,
    Class<? extends PacketFieldCodec> codecClass
) {
}
