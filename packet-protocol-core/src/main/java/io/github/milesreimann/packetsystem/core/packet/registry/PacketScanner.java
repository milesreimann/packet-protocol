package io.github.milesreimann.packetsystem.core.packet.registry;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import io.github.milesreimann.packetsystem.api.packet.Packet;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Miles
 * @since 28.08.25
 */
@Log4j2
public class PacketScanner {
    public Set<Class<? extends Packet>> scanPackage(String packageName) {
        return scanPackageInternal(packageName, false);
    }

    public Set<Class<? extends Packet>> scanPackageRecursive(String packageName) {
        return scanPackageInternal(packageName, true);
    }

    private Set<Class<? extends Packet>> scanPackageInternal(String packageName, boolean recursive) {
        log.debug("Scanning package '{}' for packet classes (recursive={})", packageName, recursive);

        Function<ClassPath, ImmutableSet<ClassPath.ClassInfo>> extractor = recursive
            ? classPath -> classPath.getTopLevelClassesRecursive(packageName)
            : classPath -> classPath.getTopLevelClasses(packageName);

        return scanWithExtractor(packageName, extractor);
    }

    private Set<Class<? extends Packet>> scanWithExtractor(
        String packageName,
        Function<ClassPath, ImmutableSet<ClassPath.ClassInfo>> classExtractor
    ) {
        ClassLoader classLoader = getClass().getClassLoader();
        Set<Class<? extends Packet>> packetClasses = new HashSet<>();

        try {
            ClassPath classPath = ClassPath.from(classLoader);
            ImmutableSet<ClassPath.ClassInfo> classInfos = classExtractor.apply(classPath);

            classInfos.forEach(classInfo -> loadAndFilterPacketClass(classInfo, packetClasses));

            log.info("Found {} packet classes in package '{}'", packetClasses.size(), packageName);
        } catch (Exception e) {
            log.error("Failed to scan package '{}' for packet classes", packageName, e);
        }

        return packetClasses;
    }

    @SuppressWarnings("unchecked")
    private void loadAndFilterPacketClass(
        ClassPath.ClassInfo classInfo,
        Set<Class<? extends Packet>> packetClasses
    ) {
        try {
            Class<?> loadedClass = classInfo.load();

            if (!isPacketClass(loadedClass)) {
                log.trace("Skipping class '{}' - does not implement Packet", loadedClass.getName());
                return;
            }

            packetClasses.add((Class<? extends Packet>) loadedClass);
            log.info("Added packet class '{}'", loadedClass.getName());
        } catch (Exception e) {
            log.warn("Failed to load class '{}': {}", classInfo.getName(), e.getMessage());
        }
    }

    private boolean isPacketClass(Class<?> clazz) {
        return Packet.class.isAssignableFrom(clazz) && !Packet.class.equals(clazz);
    }
}
