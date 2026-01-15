package io.github.milesreimann.packetsystem.api.packet.codec;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Miles
 * @since 28.08.25
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface PacketField {
    Class<? extends PacketFieldCodec> codec();
}