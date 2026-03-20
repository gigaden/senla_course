package ebookstore.security.dto;

/**
 * Дто для возврата токена после логина клиента
 * */
public record LoginResponse(String token) {
}