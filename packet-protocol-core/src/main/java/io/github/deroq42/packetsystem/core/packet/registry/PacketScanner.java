package io.github.deroq42.packetsystem.core.packet.registry;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.github.deroq42.packetsystem.api.packet.Packet;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
public class PacketScanner {
    public @NotNull Set<Class<? extends Packet>> scanPackage(@NotNull String packageName) {
        return scanPackage(packageName, false);
    }

    public @NotNull Set<Class<? extends Packet>> scanPackageRecursive(@NotNull String packageName) {
        return scanPackage(packageName, true);
    }

    private @NotNull Set<Class<? extends Packet>> scanPackage(@NotNull String packageName, boolean recursive) {
        log.debug("Scanning package '{}' for packet classes{}", packageName, recursive ? " recursively" : "");

        Function<ClassPath, ImmutableSet<ClassPath.ClassInfo>> extractor = recursive
            ? classPath -> classPath.getTopLevelClassesRecursive(packageName)
            : classPath -> classPath.getTopLevelClasses(packageName);

        return scanWithExtractor(packageName, extractor);
    }

    private @NotNull Set<Class<? extends Packet>> scanWithExtractor(
        @NotNull String packageName,
        @NotNull Function<ClassPath, ImmutableSet<ClassPath.ClassInfo>> extractor
    ) {
        ClassLoader classLoader = getClass().getClassLoader();
        Set<Class<? extends Packet>> packetClasses = new HashSet<>();

        try {
            ClassPath classPath = ClassPath.from(classLoader);
            ImmutableSet<ClassPath.ClassInfo> classes = extractor.apply(classPath);

            for (ClassPath.ClassInfo classInfo : classes) {
                try {
                    Class<?> loadedClass = classInfo.load();
                    if (Packet.class.isAssignableFrom(loadedClass)) {
                        packetClasses.add((Class<? extends Packet>) loadedClass);
                    } else {
                        log.debug("Skipping class '{}' - does not implement Packet", loadedClass.getName());
                    }
                } catch (Exception e) {
                    log.warn("Failed to load class '{}': {}", classInfo.getName(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to scan package '{}' for packet classes", packageName, e);
        }

        return packetClasses;
    }
}
