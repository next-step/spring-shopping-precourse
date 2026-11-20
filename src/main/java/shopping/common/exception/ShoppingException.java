package shopping.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ShoppingException extends RuntimeException {

    private final HttpStatus status;

    protected ShoppingException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
