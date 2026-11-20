package shopping;

public class WishNotFoundException extends RuntimeException {

    public WishNotFoundException() {
        super("위시 항목을 찾을 수 없습니다.");
    }
}
