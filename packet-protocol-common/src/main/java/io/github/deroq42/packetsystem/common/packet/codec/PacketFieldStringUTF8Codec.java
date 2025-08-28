package io.github.deroq42.packetsystem.common.packet.codec;

import java.nio.charset.StandardCharsets;

/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketFieldStringUTF8Codec extends PacketFieldStringCodec {
    public PacketFieldStringUTF8Codec() {
        super(StandardCharsets.UTF_8);
    }
}