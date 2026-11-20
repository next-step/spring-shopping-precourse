package shopping.product.exception;

public class ProfanityCheckUnavailableException extends RuntimeException {
    public ProfanityCheckUnavailableException(String message) {
        super(message);
    }

    public ProfanityCheckUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
