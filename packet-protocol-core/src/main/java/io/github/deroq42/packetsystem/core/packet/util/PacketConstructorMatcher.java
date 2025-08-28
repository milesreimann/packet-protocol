package io.github.deroq42.packetsystem.core.packet.util;

import io.github.deroq42.packetsystem.api.packet.Packet;
import io.github.deroq42.packetsystem.core.exception.PacketInstantiationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
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

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends Packet>, Constructor> NO_ARGS_CONSTRUCTOR_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    @SuppressWarnings("rawtypes")
    private static final Map<ConstructorKey, Constructor> PARAM_CONSTRUCTOR_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    public static @NotNull <P extends Packet> Constructor<P> findNoArgsConstructor(@NotNull Class<P> packetClass) throws PacketInstantiationException {
        try {
            Constructor<P> constructor = packetClass.getDeclaredConstructor();
            constructor.setAccessible(true);

            if (NO_ARGS_CONSTRUCTOR_CACHE.putIfAbsent(packetClass, constructor) == null) {
                log.trace("Cached no-args constructor for packet '{}'", packetClass.getName());
            }

            return constructor;
        } catch (NoSuchMethodException e) {
            log.error("No no-args constructor found for packet {}", packetClass.getName());
            throw new PacketInstantiationException("No no-args constructor found for packet " + packetClass, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <P extends Packet> Constructor<P> findMatchingConstructor(
        Class<P> packetClass,
        Object... params
    ) {
        ConstructorKey key = new ConstructorKey(packetClass, params);

        return PARAM_CONSTRUCTOR_CACHE.computeIfAbsent(key, _ -> {
            String packetClassName = packetClass.getName();

            Constructor<?>[] constructors = packetClass.getDeclaredConstructors();
            log.debug("Found {} constructor(s) in class '{}'", constructors.length, packetClassName);

            for (Constructor<?> constructor : constructors) {
                if (isExactMatch(constructor, params)) {
                    log.debug("Found exact matching constructor for class '{}'", packetClassName);
                    constructor.setAccessible(true);
                    return constructor;
                }
            }

            Optional<Constructor<P>> assignableMatch = findAssignableConstructor(packetClass, constructors, params);
            if (assignableMatch.isPresent()) {
                Constructor<?> constructor = assignableMatch.get();
                constructor.setAccessible(true);
                return constructor;
            }

            throwIfNoConstructorMatch(packetClassName, params);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static <P extends Packet> Optional<Constructor<P>> findAssignableConstructor(
        @NotNull Class<P> packetClass,
        @NotNull Constructor<?>[] constructors,
        @NotNull Object... params
    ) {
        for (int i = 0; i < constructors.length; i++) {
            Constructor<?> constructor = constructors[i];
            if (constructor.getParameterCount() != params.length) {
                continue;
            }

            if (isAssignableMatch(constructor, i, params)) {
                log.debug("Found assignable matching constructor for class '{}' (constructor index {})", packetClass.getName(), i);
                return Optional.of((Constructor<P>) constructor);
            }
        }

        return Optional.empty();
    }

    private static boolean isExactMatch(@NotNull Constructor<?> constructor, @NotNull Object... params) {
        if (constructor.getParameterCount() != params.length) {
            return false;
        }

        Class<?>[] expectedTypes = constructor.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            Class<?> expectedType = expectedTypes[i];

            if (!param.getClass().equals(expectedType) && !isDirectWrapperMatch(expectedType, param.getClass())) {
                return false;
            }
        }

        return true;
    }

    private static boolean isAssignableMatch(
        @NotNull Constructor<?> constructor,
        int constructorIndex,
        @NotNull Object... params
    ) {
        Class<?>[] expectedTypes = constructor.getParameterTypes();

        for (int paramIndex = 0; paramIndex < params.length; paramIndex++) {
            Object param = params[paramIndex];
            Class<?> expectedType = expectedTypes[paramIndex];

            Class<?> actualType = param.getClass();
            if (!isTypeAssignable(expectedType, actualType)) {
                log.trace("Constructor {} parameter {} expects type '{}', but got '{}'",
                    constructorIndex, paramIndex, expectedType.getSimpleName(), actualType.getSimpleName());
                return false;
            }
        }

        return true;
    }

    private static boolean isTypeAssignable(@NotNull Class<?> expectedType, @NotNull Class<?> actualType) {
        if (expectedType.isAssignableFrom(actualType)) {
            return true;
        }

        return isDirectWrapperMatch(expectedType, actualType) || isDirectWrapperMatch(actualType, expectedType);
    }

    private static boolean isDirectWrapperMatch(@NotNull Class<?> primitive, @NotNull Class<?> wrapper) {
        return PRIMITIVE_TO_WRAPPER.get(primitive) == wrapper;
    }

    private static void throwIfNoConstructorMatch(@NotNull String packetClassName, Object @NotNull ... params)  {
        String paramTypeNames = Arrays.stream(params)
            .map(p -> p.getClass().getName())
            .collect(Collectors.joining(", "));

        log.error(
            "No matching constructor found for class '{}' with parameter types [{}]",
            packetClassName,
            paramTypeNames
        );

        throw new IllegalStateException("No constructor found with matching parameters for class " + packetClassName);
    }
}