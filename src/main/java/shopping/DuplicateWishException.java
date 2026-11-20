package shopping;

public class DuplicateWishException extends RuntimeException {

    public DuplicateWishException() {
        super("이미 위시 리스트에 등록된 상품입니다.");
    }
}
