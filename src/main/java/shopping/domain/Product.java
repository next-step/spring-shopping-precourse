package shopping.domain;

public record Product(Long id, String name, long price, String imageUrl) {

    private static final String ALLOWED_NAME_PATTERN = "^[a-zA-Z가-힣0-9 ()\\[\\]+\\-&/_]*$";

    public Product {
        if (name == null || name.length() > 15) {
            throw new IllegalArgumentException("상품 이름은 공백을 포함하여 최대 15자까지 입력 가능합니다.");
        }
        if (!name.matches(ALLOWED_NAME_PATTERN)) {
            throw new IllegalArgumentException("상품 이름에 허용되지 않은 특수 문자가 포함되어 있습니다.");
        }
    }
}
