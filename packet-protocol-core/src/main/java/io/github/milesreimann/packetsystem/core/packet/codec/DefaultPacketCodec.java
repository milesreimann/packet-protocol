package io.github.milesreimann.packetsystem.core.packet.codec;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.api.packet.codec.PacketCodec;
import io.github.milesreimann.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
@RequiredArgsConstructor
public class DefaultPacketCodec implements PacketCodec {
    private final PacketEncoder packetEncoder;
    private final PacketDecoder packetDecoder;
    private final PacketFieldRegistry packetFieldRegistry;

    public void encode(Packet packet, ByteBuf byteBuf) {
        packetEncoder.encode(packet, byteBuf);
    }

    public Packet decode(ByteBuf byteBuf) {
        return packetDecoder.decode(byteBuf);
    }

    @Override
    public <T> void addFieldCodec(PacketFieldCodec<T> fieldCodec) {
        packetFieldRegistry.registerFieldCodec(fieldCodec);
    }
}