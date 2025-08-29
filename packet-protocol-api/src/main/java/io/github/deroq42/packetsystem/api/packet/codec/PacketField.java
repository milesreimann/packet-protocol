package io.github.deroq42.packetsystem.api.packet.codec;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field of a {@link io.github.deroq42.packetsystem.api.packet.Packet}
 * to be included in packet serialization and deserialization.
 * <p>
 * Each annotated field must specify a {@link PacketFieldCodec} implementation
 * that handles encoding and decoding of the field type.
 * </p>
 *
 * <p>
 * This annotation is retained at runtime and intended for use by packet codecs.
 * </p>
 *
 * @author Miles
 * @since 28.08.25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PacketField {
    /**
     * The {@link PacketFieldCodec} to use for encoding and decoding this field.
     *
     * @return the codec class, never {@code null}
     */
    @NotNull Class<? extends PacketFieldCodec> codec();
}
