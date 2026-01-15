package io.github.milesreimann.packetsystem.api.packet.codec;


/**
 * @author Miles
 * @since 28.08.25
 */
public interface PacketCodec {
    <T> void addFieldCodec(PacketFieldCodec<T> fieldCodec);
}
