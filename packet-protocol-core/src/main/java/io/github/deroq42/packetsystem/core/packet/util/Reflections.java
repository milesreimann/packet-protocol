package io.github.deroq42.packetsystem.core.packet.util;

import io.github.deroq42.packetsystem.core.exception.ReflectionException;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author Miles
 * @since 28.08.25
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Reflections {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final Map<Field, MethodHandle> FIELD_GETTERS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Field, MethodHandle> FIELD_SETTERS = Collections.synchronizedMap(new WeakHashMap<>());

    public static @Nullable Object readField(@NotNull Field field, @Nullable Object instance) {
        try {
            MethodHandle getter = getOrCreateGetter(field);
            return getter.invoke(instance);
        } catch (Throwable cause) {
            throw new ReflectionException("Failed to read field " + field, cause);
        }
    }

    public static void writeField(@NotNull Field field, @Nullable Object instance, @Nullable Object value) {
        try {
            MethodHandle setter = getOrCreateSetter(field);
            setter.invoke(instance, value);
        } catch (Throwable cause) {
            throw new ReflectionException("Failed to write field " + field, cause);
        }
    }

    private static @NotNull MethodHandle getOrCreateGetter(@NotNull Field field) {
        return FIELD_GETTERS.computeIfAbsent(field, f -> {
            try {
                f.setAccessible(true);
                return LOOKUP.unreflectGetter(f);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Unable to create getter for field " + f, e);
            }
        });
    }

    private static @NotNull MethodHandle getOrCreateSetter(@NotNull Field field) {
        return FIELD_SETTERS.computeIfAbsent(field, f -> {
            try {
                f.setAccessible(true);
                return LOOKUP.unreflectSetter(f);
            } catch (IllegalAccessException e) {
                throw new ReflectionException("Unable to create setter for field " + f, e);
            }
        });
    }
}
