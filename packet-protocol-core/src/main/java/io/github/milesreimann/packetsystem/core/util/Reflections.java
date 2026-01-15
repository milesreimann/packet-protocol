package io.github.milesreimann.packetsystem.core.util;

import io.github.milesreimann.packetsystem.core.exception.ReflectionException;
import lombok.NoArgsConstructor;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Reflections {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Field, MethodHandle> FIELD_GETTERS = new ConcurrentHashMap<>();
    private static final Map<Field, MethodHandle> FIELD_SETTERS = new ConcurrentHashMap<>();

    public static Object readField(Field field, Object instance) {
        try {
            return getGetter(field).invoke(instance);
        } catch (Throwable t) {
            throw new ReflectionException("Failed to read field " + formatField(field), t);
        }
    }

    public static void writeField(Field field, Object instance, Object value) {
        try {
            getSetter(field).invoke(instance, value);
        } catch (Throwable t) {
            throw new ReflectionException("Failed to write field " + formatField(field), t);
        }
    }

    private static MethodHandle getGetter(Field field) {
        return FIELD_GETTERS.computeIfAbsent(field, Reflections::createGetter);
    }

    private static MethodHandle getSetter(Field field) {
        return FIELD_SETTERS.computeIfAbsent(field, Reflections::createSetter);
    }

    private static MethodHandle createGetter(Field field) {
        try {
            MethodHandles.Lookup privateLookup =
                MethodHandles.privateLookupIn(field.getDeclaringClass(), LOOKUP);
            return privateLookup.unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Unable to create getter for field " + formatField(field), e);
        }
    }

    private static MethodHandle createSetter(Field field) {
        try {
            MethodHandles.Lookup privateLookup =
                MethodHandles.privateLookupIn(field.getDeclaringClass(), LOOKUP);
            return privateLookup.unreflectSetter(field);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Unable to create setter for field " + formatField(field), e);
        }
    }

    private static String formatField(Field field) {
        return field.getDeclaringClass().getName() + "#" + field.getName();
    }
}
