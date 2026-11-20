package shopping.product.application;

public final class ProfanityCheckUnavailableException extends RuntimeException {
    public ProfanityCheckUnavailableException(String message) {
        super(message);
    }

    public ProfanityCheckUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
