package io.github.deroq42.packetsystem.core.packet.codec;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.api.packet.codec.PacketCodec;
import io.github.deroq42.packetsystem.api.packet.codec.PacketFieldCodec;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
@RequiredArgsConstructor
public class DefaultPacketCodec implements PacketCodec {
    private final @NotNull PacketEncoder packetEncoder;
    private final @NotNull PacketDecoder packetDecoder;
    private final @NotNull PacketFieldRegistry packetFieldRegistry;

    public void encode(@NotNull Packet packet, @NotNull ByteBuf byteBuf) {
        packetEncoder.encode(packet, byteBuf);
    }

    public @NotNull Packet decode(@NotNull ByteBuf byteBuf) {
        return packetDecoder.decode(byteBuf);
    }

    @Override
    public <T> void addFieldCodec(@NotNull PacketFieldCodec<T> fieldCodec) {
        packetFieldRegistry.addFieldCodec(fieldCodec);
    }
}