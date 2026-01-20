package config.processor;

import config.annotation.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Класс обрабатывает аннотации для пропертей и инжектит их
 */
public class ConfigurationInjector {

    /**
     * Класс инжектит нужные проперти в поля
     */
    public static void inject(Object object) {
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                String configFileName = annotation.configFileName();
                String propertyName = annotation.propertyName();
                Class<?> type = annotation.type();

                if (propertyName.isEmpty()) {
                    propertyName = clazz.getSimpleName() + "." + field.getName();
                }

                String value = getProperty(configFileName, propertyName);
                if (value != null) {
                    try {
                        field.setAccessible(true);
                        Object convertedValue = convert(value, field.getType(), type);
                        field.set(object, convertedValue);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to inject property", e);
                    }
                }
            }
        }
    }

    /**
     * Получаем значение из проперти по ключу
     *
     * @param configFileName - файла, откуда будем брать проперти
     * @param propertyName   - ключ, по которому нужно получить значение из пропертей
     */
    private static String getProperty(String configFileName, String propertyName) {
        Properties properties = new Properties();
        try (InputStream input = ConfigurationInjector.class.getClassLoader()
                .getResourceAsStream(configFileName)) {
            if (input == null) {
                return null;
            }
            properties.load(input);
            return properties.getProperty(propertyName);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла настроек: " + configFileName, e);
        }
    }

    /**
     * По типу поля приводим значение проперти к нужному типу
     *
     * @param value          - значение, которое нужно привести
     * @param fieldType      - тип поля
     * @param annotationType - тип аннотации
     */
    private static Object convert(String value, Class<?> fieldType, Class<?> annotationType) {
        Class<?> targetType = (annotationType != String.class) ? annotationType : fieldType;

        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (targetType == List.class && fieldType == List.class) {
            return Arrays.asList(value.split(","));
        } else if (targetType.isArray()) {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
        }
        throw new IllegalArgumentException("Unsupported type: " + targetType);
    }
}