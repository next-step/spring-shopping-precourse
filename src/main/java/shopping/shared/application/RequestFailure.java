package shopping.shared.application;

public final class RequestFailure extends RuntimeException {
    public enum Reason {
        INVALID_INPUT, INVALID_CREDENTIALS, AUTHENTICATION_REQUIRED, NOT_FOUND, CONFLICT
    }

    private final Reason reason;

    public RequestFailure(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
