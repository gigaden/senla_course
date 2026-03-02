package ebookstore.exception;

public abstract class BaseException extends RuntimeException {

    private String reason;

    public BaseException(String message) {
        super(message);
    }

    public String getReason() {
        return reason;
    }
}