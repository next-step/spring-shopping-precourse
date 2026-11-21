package shopping.wish.exception;

public class ForbiddenWishException extends RuntimeException {

    public ForbiddenWishException() {
        super("다른 회원의 위시에 접근할 수 없습니다.");
    }
}
