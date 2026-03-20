package ebookstore.exception.notfound;

import ebookstore.exception.BaseException;

public class ClientLoginException extends BaseException {

    private final String reason = "Ошибка авторизации клиента";

    public ClientLoginException(String message) {
        super(message);
    }
}