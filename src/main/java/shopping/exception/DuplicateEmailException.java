package shopping.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Email already exists");
    }

    public DuplicateEmailException(Throwable cause) {
        super("Email already exists", cause);
    }
}
