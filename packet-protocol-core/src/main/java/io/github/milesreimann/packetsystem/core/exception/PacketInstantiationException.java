package io.github.milesreimann.packetsystem.core.exception;


/**
 * @author Miles
 * @since 28.08.25
 */
public class PacketInstantiationException extends RuntimeException {
    public PacketInstantiationException(String message, Exception e) {
        super(message, e);
    }
}