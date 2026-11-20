package shopping;

import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

@NullMarked
public record Product(
        Long id,
        String name,
        long price,
        String url
) {
    private static final Pattern VALID_PATTERN =
            Pattern.compile("^[a-zA-Z0-9가-힣\\s()\\[\\]+&/_-]+$");
    public static final int MAX_NAME_LENGTH = 15;


    public Product {
        if (name.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }
        if (!VALID_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException();
        }
        if (price < 0) {
            throw new IllegalArgumentException("상품 가격은 0원 이상이어야 합니다");
        }
    }
}
