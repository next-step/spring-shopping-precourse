package shopping.external;

public class ProfanityCheckException extends RuntimeException {

    public ProfanityCheckException(String message) {
        super(message);
    }

    public ProfanityCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
