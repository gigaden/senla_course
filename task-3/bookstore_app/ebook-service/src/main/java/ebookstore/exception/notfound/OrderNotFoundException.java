package ebookstore.exception.notfound;

import ebookstore.exception.BaseException;

public class OrderNotFoundException extends BaseException {

    private final String reason = "Ошибка поиска заказа";

    public OrderNotFoundException(String message) {
        super(message);
    }
}
