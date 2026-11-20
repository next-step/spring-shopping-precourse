package shopping.controller;

import java.math.BigDecimal;

public record WishRequest(BigDecimal productId) {
    public long productIdValue() {
        if (productId == null) {
            throw new IllegalArgumentException("상품 번호를 입력해 주세요.");
        }
        try {
            return productId.longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("상품 번호는 숫자(정수)여야 합니다.", e);
        }
    }
}
