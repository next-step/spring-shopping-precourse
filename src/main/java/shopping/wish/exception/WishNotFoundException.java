package shopping.wish.exception;

public class WishNotFoundException extends RuntimeException {

    public WishNotFoundException() {
        super("존재하지 않는 위시입니다.");
    }
}
