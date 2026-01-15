package io.github.milesreimann.packetsystem.core.exception;


/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketDecodeException  extends RuntimeException {
    public PacketDecodeException( Exception e) {
        super("Failed to decode packet", e);
    }

    public PacketDecodeException(String message) {
        super("Failed to decode packet: " + message);
    }
}
