package shopping.domain;

public class ProfanityCheckException extends RuntimeException {
    public enum Reason {
        BAD_REQUEST,
        SERVICE_ERROR,
        CONNECTION_ERROR,
        INVALID_RESPONSE
    }

    private final Reason reason;

    public ProfanityCheckException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public ProfanityCheckException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
