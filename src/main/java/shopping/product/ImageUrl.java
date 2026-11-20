package shopping.product;

import java.util.Objects;
import java.util.regex.Pattern;

public class ImageUrl {
    private final String value;
    private static final int MAX_LENGTH = 255;
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://\\S+$");

    public ImageUrl(String value) {
        validateNotBlank(value);
        validateLength(value);
        validateUrlFormat(value);
        this.value = value;
    }

    private void validateNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이미지 URL은 비어있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("이미지 URL은 최대 255자까지 입력할 수 있습니다.");
        }
    }

    private void validateUrlFormat(String value) {
        if (!URL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("이미지 URL은 http 또는 https로 시작해야 합니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override 
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageUrl other)) return false;

        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
