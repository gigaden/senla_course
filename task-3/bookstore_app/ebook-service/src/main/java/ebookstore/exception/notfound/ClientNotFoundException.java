package ebookstore.exception.notfound;

import ebookstore.exception.BaseException;

public class ClientNotFoundException extends BaseException {

    private final String reason = "Ошибка поиска клиента";

    public ClientNotFoundException(String message) {
        super(message);
    }
}
