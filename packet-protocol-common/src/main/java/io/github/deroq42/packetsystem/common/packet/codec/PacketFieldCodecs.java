package io.github.deroq42.packetsystem.common.packet.codec;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PacketFieldCodecs {
    public static final PacketFieldStringUTF8Codec STRING_UTF8_CODEC = new PacketFieldStringUTF8Codec();
    public static final PacketFieldIntegerCodec INTEGER_CODEC = new PacketFieldIntegerCodec();
    public static final PacketFieldUUIDCodec UUID_CODEC = new PacketFieldUUIDCodec();
    public static final PacketFieldByteArrayCodec BYTE_ARRAY_CODEC = new PacketFieldByteArrayCodec();
}