package io.github.milesreimann.packetsystem.core.packet.factory;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.core.exception.PacketInstantiationException;
import io.github.milesreimann.packetsystem.core.util.PacketConstructorMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Miles
 * @since 28.08.25
 */
@RequiredArgsConstructor
@Log4j2
public class PacketFactory {
    public <P extends Packet> P create(Class<P> packetClass) {
        log.debug("Creating packet instance of class '{}' with no-args constructor", packetClass.getName());

        Constructor<? extends Packet> constructor = PacketConstructorMatcher.findNoArgsConstructor(packetClass);
        return instantiatePacket(constructor, packetClass);
    }

    public <P extends Packet> P create(
        Class<P> packetClass,
        Object... params
    ) throws IllegalStateException {
        log.debug("Creating packet instance of class '{}' with {} parameters", packetClass.getName(), params.length);

        Constructor<? extends Packet> constructor = PacketConstructorMatcher.findMatchingConstructor(packetClass, params);
        return instantiatePacket(constructor, packetClass, params);
    }

    @SuppressWarnings("unchecked")
    private <P extends Packet> P instantiatePacket(
        Constructor<? extends Packet> constructor,
        Class<P> packetClass,
        Object... params
    ) throws PacketInstantiationException {
        String packetClassName = packetClass.getName();

        try {
            P packet = (P) constructor.newInstance(params);
            log.debug("Successfully created packet instance of class '{}'", packetClassName);
            return packet;
        } catch (InvocationTargetException e) {
            log.error("Constructor threw exception while creating packet '{}'", packetClassName, e);
            throw new PacketInstantiationException("Constructor failed for packet " + packetClass, e);
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate packet '{}'", packetClassName, e);
            throw new PacketInstantiationException("Cannot instantiate packet " + packetClass, e);
        } catch (IllegalArgumentException e) {
            log.error("Invalid arguments for packet constructor '{}'", packetClassName, e);
            throw new PacketInstantiationException("Invalid constructor arguments for packet " + packetClass, e);
        }
    }
}