package ebookstore.exception;

public class DatabaseException extends BaseException {
    private final String reason = "Ошибка при запросе к базе данных";

    public DatabaseException(String message) {
        super(message);
    }
}
