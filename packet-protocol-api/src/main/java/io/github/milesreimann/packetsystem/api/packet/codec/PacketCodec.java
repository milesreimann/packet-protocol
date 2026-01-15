package io.github.milesreimann.packetsystem.api.packet.codec;

/**
 * Codec for encoding and decoding {@link io.github.milesreimann.packetsystem.api.packet.Packet}s.
 * <p>
 * A PacketCodec handles serialization and deserialization of packets
 * and allows registering custom field codecs to support additional data types.
 * </p>
 */
public interface PacketCodec {
    /**
     * Registers a field codec for encoding and decoding a specific type.
     * <p>
     * Field codecs define how individual packet fields are serialized
     * to and deserialized from the underlying byte buffer.
     * </p>
     *
     * @param fieldCodec the field codec to add, not null
     * @param <T>        the type handled by this codec
     */
    <T> void addFieldCodec(PacketFieldCodec<T> fieldCodec);
}
