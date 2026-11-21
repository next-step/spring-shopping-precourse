package shopping;

final class WishNotFoundException extends IllegalArgumentException {
    WishNotFoundException() {
        super("존재하지 않는 위시 항목입니다");
    }
}
