package ebookstore.exception;

public class PropertiesLoadException extends BaseException {

    private final String reason = "Ошибка при загрузке из файла настроек";

    public PropertiesLoadException(String message) {
        super(message);
    }
}
