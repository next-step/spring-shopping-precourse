package shopping.product.domain;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import shopping.product.exception.InvalidProductNameException;

@Component
public class ProductNameValidator {
    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED_PATTERN =
            Pattern.compile("^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]+$");

    public void validate(String name) {
        validateNotBlank(name);
        validateLength(name);
        validateCharacters(name);
    }

    private void validateNotBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductNameException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateLength(String name) {
        if (name.length() > MAX_LENGTH) {
            throw new InvalidProductNameException("상품 이름은 15자 이하여야 합니다.");
        }
    }

    private void validateCharacters(String name) {
        if (!ALLOWED_PATTERN.matcher(name).matches()) {
            throw new InvalidProductNameException("상품 이름에는 허용되지 않는 문자가 포함되어 있습니다.");
        }
    }
}
