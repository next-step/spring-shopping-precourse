package shopping.product.exception;

public class ProfanityProductNameException extends RuntimeException {

    public ProfanityProductNameException() {
        super("상품 이름에 비속어가 포함되어 있습니다.");
    }
}
