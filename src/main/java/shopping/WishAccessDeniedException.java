package shopping;

public class WishAccessDeniedException extends RuntimeException {

    public WishAccessDeniedException() {
        super("다른 회원의 위시 항목을 삭제할 수 없습니다.");
    }
}
