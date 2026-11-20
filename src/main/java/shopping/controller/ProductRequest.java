package shopping.controller;

import java.math.BigDecimal;

public record ProductRequest(String name, BigDecimal price, String imageUrl) {
    public long priceValue() {
        if (price == null) {
            throw new IllegalArgumentException("상품 가격을 입력해 주세요.");
        }
        try {
            return price.longValueExact();
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("상품 가격은 소수가 아닌 숫자로 입력해 주세요.", e);
        }
    }
}
