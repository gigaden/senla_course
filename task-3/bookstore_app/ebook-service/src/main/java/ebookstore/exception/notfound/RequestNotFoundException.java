package ebookstore.exception.notfound;

import ebookstore.exception.BaseException;

public class RequestNotFoundException extends BaseException {
    private final String reason = "Ошибка поиска запроса";

    public RequestNotFoundException(String message) {
        super(message);
    }
}
