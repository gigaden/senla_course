package ebookstore.exception.notfound;

import ebookstore.exception.BaseException;

public class BookNotFoundException extends BaseException {
    private final String reason = "Ошибка поиска книги";

    public BookNotFoundException(String message) {
        super(message);
    }
}