package shopping;

import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

@NullMarked
public record Domain(String value) {
    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public Domain {
        if (!DOMAIN_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효하지 않은 도메인 형식입니다: " + value);
        }
    }
}