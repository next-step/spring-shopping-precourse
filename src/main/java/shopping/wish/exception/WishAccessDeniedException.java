package shopping.wish.exception;

public class WishAccessDeniedException extends RuntimeException{
    public WishAccessDeniedException(String message) {
        super(message);
    }
}
