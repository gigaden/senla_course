package ebookstore.exception;

public class DatesValidationException extends BaseException {

    private final String reason = "Ошибка при проверке дат в запросе";

    public DatesValidationException(String message) {
        super(message);
    }
}
