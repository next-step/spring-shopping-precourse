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

    public static Password create(
            String rawPassword,
            PasswordEncoder hashing
    ) {
        if (rawPassword.length() < 10) {
            throw new IllegalArgumentException();
        }
        return new Password(hashing.hash(rawPassword));
    }

    public boolean matches(
            String rawPassword,
            PasswordEncoder hashing
    ) {
        return hashing.matches(rawPassword, value);
    }
}
