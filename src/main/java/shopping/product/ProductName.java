package shopping.product;

import java.util.regex.Pattern;
import java.util.Objects;

// @Embeddable
public class ProductName {

    private static final int MAX_LENGTH = 15;
    private static final Pattern ALLOWED = Pattern.compile("^[\\p{L}\\p{N} ()\\[\\]+\\-&/_]*$");

    // @Column(name = "name", nullable = false, length = MAX_LENGTH)
    private String value;

    // protected ProductName() {
    // }

    public ProductName(String value) {
        validateNotBlank(value);
        validateLength(value);
        validateCharacters(value);
        this.value = value;
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("상품 이름은 최대 15자까지 입력할 수 있습니다.");
        }
    }

    private void validateCharacters(String value) {
        if (!ALLOWED.matcher(value).matches()) {
            throw new IllegalArgumentException("허용되지 않은 특수 문자가 포함되어 있습니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductName other)) return false;

        return Objects.equals(value, other.value);
    }
    @Override 
    public int hashCode() {
        return Objects.hash(value);
    }
}