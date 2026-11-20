package shopping.exception;

public class DuplicateWishException extends RuntimeException {
    public DuplicateWishException(String message) {
        super(message);
    }

    public DuplicateWishException(String message, Throwable cause) {
        super(message, cause);
    }
}
