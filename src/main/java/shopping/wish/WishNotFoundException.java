package shopping.wish;

public class WishNotFoundException extends RuntimeException {

    public WishNotFoundException(Long id) {
        super("위시리스트 항목을 찾을 수 없습니다. id=" + id);
    }
}