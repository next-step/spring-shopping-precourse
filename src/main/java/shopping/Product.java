package shopping;

import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

@NullMarked
public record Product(
        Long id,
        String name,
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
    }
}
