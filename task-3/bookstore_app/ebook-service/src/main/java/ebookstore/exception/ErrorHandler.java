package ebookstore.exception;

import ebookstore.exception.notfound.BookNotFoundException;
import ebookstore.exception.notfound.ClientNotFoundException;
import ebookstore.exception.notfound.OrderNotFoundException;
import ebookstore.exception.notfound.RequestNotFoundException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Класс отлавливает исключения и возвращает ответ в нужном формате
 */
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler({
            BookNotFoundException.class,
            ClientNotFoundException.class,
            OrderNotFoundException.class,
            RequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final BaseException e, WebRequest request) {
        log.error("Ошибка 404 {}: {} в запросе {}",
                e.getClass().getSimpleName(), e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
    }

    @ExceptionHandler({
            PropertiesLoadException.class,
            DatabaseException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleConnectionException(final BaseException e, WebRequest request) {
        log.error("Ошибка 500 {}: {} в запросе {}",
                e.getClass().getSimpleName(), e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getReason());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ValidationException.class,
            NumberFormatException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
            DatesValidationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidMethodArgument(Exception e, WebRequest request) {
        log.error("Ошибка  400 {}: {} в запросе {}",
                e.getClass(), e.getMessage(), request.getDescription(false));
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "Неверный формат запроса");
    }

    public Map<String, String> buildErrorResponse(Exception e, HttpStatus status, String reason) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", status.name());
        response.put("reason", reason);
        response.put("message", e.getMessage());
        response.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return response;
    }
}
