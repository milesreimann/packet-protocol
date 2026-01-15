package io.github.milesreimann.packetsystem.core.util;

import io.github.milesreimann.packetsystem.api.packet.Packet;
import io.github.milesreimann.packetsystem.core.exception.PacketInstantiationException;
import io.github.milesreimann.packetsystem.core.model.ConstructorKey;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class PacketConstructorMatcher {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
        int.class, Integer.class,
        long.class, Long.class,
        double.class, Double.class,
        float.class, Float.class,
        boolean.class, Boolean.class,
        byte.class, Byte.class,
        char.class, Character.class,
        short.class, Short.class,
        void.class, Void.class
    );

    private static final Map<Class<? extends Packet>, Constructor<? extends Packet>> NO_ARGS_CACHE = new ConcurrentHashMap<>();
    private static final Map<ConstructorKey, Constructor<? extends Packet>> PARAMETERIZED_CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <P extends Packet> Constructor<P> findNoArgsConstructor(Class<P> packetClass) {
        return (Constructor<P>) NO_ARGS_CACHE.computeIfAbsent(
            packetClass,
            PacketConstructorMatcher::createNoArgsConstructor
        );
    }

    @SuppressWarnings("unchecked")
    public static <P extends Packet> Constructor<P> findMatchingConstructor(
        Class<P> packetClass,
        Object... params
    ) {
        ConstructorKey key = new ConstructorKey(packetClass, params);

        return (Constructor<P>) PARAMETERIZED_CACHE.computeIfAbsent(
            key,
            _ -> findConstructorForParameters(packetClass, params)
        );
    }

    private static <P extends Packet> Constructor<? extends Packet> createNoArgsConstructor(
        Class<P> packetClass
    ) {
        try {
            Constructor<? extends Packet> constructor = packetClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            log.trace("Cached no-args constructor for packet '{}'", packetClass.getName());

            return constructor;
        } catch (NoSuchMethodException e) {
            throw new PacketInstantiationException(
                "No no-args constructor found for packet " + packetClass.getName(),
                e
            );
        }
    }

    private static <P extends Packet> Constructor<? extends Packet> findConstructorForParameters(
        Class<P> packetClass,
        Object... params
    ) {
        String packetClassName = packetClass.getName();
        Constructor<?>[] constructors = packetClass.getDeclaredConstructors();

        log.debug(
            "Searching {} constructor(s) in class '{}' for matching parameters",
            constructors.length,
            packetClassName
        );

        return findExactMatch(constructors, params)
            .map(exactConstructor -> {
                log.debug("Found exact matching constructor for class '{}'", packetClassName);
                return makeAccessible(exactConstructor);
            })
            .orElseGet(() -> {
                return findAssignableMatch(packetClass, constructors, params)
                    .map(assignableConstructor -> makeAccessible(assignableConstructor))
                    .orElseThrow(() -> createNoMatchException(packetClassName, params));
            });
    }

    private static Optional<Constructor<?>> findExactMatch(
        Constructor<?>[] constructors,
        Object... params
    ) {
        return Arrays.stream(constructors)
            .filter(constructor -> isExactMatch(constructor, params))
            .findFirst();
    }

    @SuppressWarnings("unchecked")
    private static <P extends Packet> Optional<Constructor<P>> findAssignableMatch(
        Class<P> packetClass,
        Constructor<?>[] constructors,
        Object... params
    ) {
        for (int i = 0; i < constructors.length; i++) {
            Constructor<?> constructor = constructors[i];

            if (!hasMatchingParameterCount(constructor, params)) {
                continue;
            }

            if (isAssignableMatch(constructor, i, params)) {
                log.debug(
                    "Found assignable matching constructor for class '{}' (constructor index {})",
                    packetClass.getName(),
                    i
                );
                return Optional.of((Constructor<P>) constructor);
            }
        }

        return Optional.empty();
    }

    private static boolean hasMatchingParameterCount(Constructor<?> constructor, Object... params) {
        return constructor.getParameterCount() == params.length;
    }

    private static boolean isExactMatch(Constructor<?> constructor, Object... params) {
        if (!hasMatchingParameterCount(constructor, params)) {
            return false;
        }

        Class<?>[] expectedTypes = constructor.getParameterTypes();

        for (int i = 0; i < params.length; i++) {
            if (!isExactTypeMatch(expectedTypes[i], params[i])) {
                return false;
            }
        }

        return true;
    }

    private static boolean isExactTypeMatch(Class<?> expectedType, Object param) {
        Class<?> actualType = param.getClass();
        return actualType.equals(expectedType) || isDirectWrapperMatch(expectedType, actualType);
    }

    private static boolean isAssignableMatch(
        Constructor<?> constructor,
        int constructorIndex,
        Object... params
    ) {
        Class<?>[] expectedTypes = constructor.getParameterTypes();

        for (int i = 0; i < params.length; i++) {
            if (!isParameterAssignable(expectedTypes[i], params[i], constructorIndex, i)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isParameterAssignable(
        Class<?> expectedType,
        Object param,
        int constructorIndex,
        int paramIndex
    ) {
        Class<?> actualType = param.getClass();

        if (isTypeAssignable(expectedType, actualType)) {
            return true;
        }

        log.trace(
            "Constructor {} parameter {} expects type '{}', but got '{}'",
            constructorIndex,
            paramIndex,
            expectedType.getName(),
            actualType.getName()
        );

        return false;
    }

    private static boolean isTypeAssignable(Class<?> expectedType, Class<?> actualType) {
        return expectedType.isAssignableFrom(actualType)
            || isDirectWrapperMatch(expectedType, actualType)
            || isDirectWrapperMatch(actualType, expectedType);
    }

    private static boolean isDirectWrapperMatch(Class<?> primitive, Class<?> wrapper) {
        return PRIMITIVE_TO_WRAPPER.get(primitive) == wrapper;
    }

    private static Constructor<? extends Packet> makeAccessible(Constructor<?> constructor) {
        constructor.setAccessible(true);
        return (Constructor<? extends Packet>) constructor;
    }

    private static IllegalStateException createNoMatchException(String packetClassName, Object... params) {
        String parameterTypes = formatParameterTypes(params);

        log.warn(
            "No matching constructor found for class '{}' with parameter types [{}]",
            packetClassName,
            parameterTypes
        );

        throw new IllegalStateException("No constructor found with matching parameters for class " + packetClassName);
    }

    private static String formatParameterTypes(Object... params) {
        return Arrays.stream(params)
            .map(param -> param.getClass().getName())
            .collect(Collectors.joining(", "));
    }
}