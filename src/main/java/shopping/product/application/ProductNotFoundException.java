package shopping.product.application;

public final class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(long id) {
        super("상품을 찾을 수 없습니다. 상품 ID: " + id);
    }
}
