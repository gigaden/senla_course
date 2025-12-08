package ebookstore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

/**
 * Класс для сериализации и десериализации данных
 * <p>
 * Предоставляет статические методы для сохранения объектов в файлы и их восстановления.
 * Использует стандартную Java сериализацию для работы с любыми объектами, реализующими
 * интерфейс {@link java.io.Serializable}.
 */
public class DataSerializer {

    /**
     * Сериализует объект в файл
     *
     * @param <T>      тип сериализуемого объекта
     * @param data     объект для сериализации
     * @param filename имя файла для сохранения
     * @return true если сериализация прошла успешно, false в случае ошибки
     */
    public static <T> boolean serializeData(T data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            System.out.println("Ошибка сериализации: " + e.getMessage());
            return false;
        }
    }

    /**
     * Десериализует объект из файла
     *
     * @param <T>      тип десериализуемого объекта
     * @param filename имя файла для чтения
     * @return Optional с десериализованным объектом или пустой Optional если файл не существует или произошла ошибка
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> deserializeData(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return Optional.empty();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return Optional.of((T) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка десериализации: " + e.getMessage());
            return Optional.empty();
        }
    }
}