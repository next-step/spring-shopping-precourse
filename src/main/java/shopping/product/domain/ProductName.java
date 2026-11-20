package shopping.product.domain;

import java.util.regex.Pattern;

public record ProductName(String name) {
    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED_CHARACTERS =
        Pattern.compile("[\\p{L}\\p{Nd} ()\\[\\]+&/_-]+");

    public ProductName {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름을 입력해 주세요.");
        }
        if (name.codePointCount(0, name.length()) > MAX_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 15자 이하로 입력해 주세요.");
        }
        if (!ALLOWED_CHARACTERS.matcher(name).matches()) {
            throw new IllegalArgumentException("상품 이름에 허용되지 않는 문자가 있습니다.");
        }
    }
}
