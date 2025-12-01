package ebookstore.util;

import ebookstore.exception.PropertiesLoadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс обрабатывает наши проперти из файла
 */
public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }

    /**
     * Загружаем наш проперти из файла
     *
     * @throws PropertiesLoadException - в случае, если файл отсутствует
     */
    private static void loadProperties() {
        try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            PROPERTIES.load(is);
        } catch (IOException e) {
            throw new PropertiesLoadException(e.getMessage());
        }
    }

    /**
     * Получаем значение из файла проперти
     *
     * @param key - ключ, по которому нужно получить значение
     * @return - искомое значение из файла
     */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }
}
