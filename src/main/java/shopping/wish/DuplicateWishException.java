package shopping.wish;

public class DuplicateWishException extends RuntimeException {

    public DuplicateWishException() {
        super("이미 위시리스트에 담긴 상품입니다.");
    }
}