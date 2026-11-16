package shopping;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record Password(
        String value
) {
    public Password {
        if (value.length() < 10) {
            throw new IllegalArgumentException();
        }
        // 이외에 특수문자를 포함해야 한다거나 등등의 정책.
    }
}
