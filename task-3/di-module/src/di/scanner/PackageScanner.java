package di.scanner;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

public class PackageScanner {

    public static Set<Class<?>> findClassesWithAnnotation(String packageName,
                                                          Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (directory.exists()) {
                    classes.addAll(findClasses(directory, packageName, annotation));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan packages", e);
        }

        return classes;
    }

    private static Set<Class<?>> findClasses(File directory, String packageName,
                                             Class<? extends Annotation> annotation) {
        Set<Class<?>> classes = new HashSet<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName(), annotation));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(annotation)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Игнорируем классы, которые не могут быть загружены
                }
            }
        }

        return classes;
    }
}