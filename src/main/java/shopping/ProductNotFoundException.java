package shopping;

final class ProductNotFoundException extends IllegalArgumentException {
    ProductNotFoundException() {
        super("존재하지 않는 상품입니다");
    }
}
