package shopping.common.error;

public class ShoppingException extends RuntimeException {

    private final ErrorCode code;

    public ShoppingException(ErrorCode code) {
        super(code.message());
        this.code = code;
    }

    public ErrorCode code() {
        return code;
    }
}
