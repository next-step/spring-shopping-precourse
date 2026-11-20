package shopping.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;
import java.util.regex.Pattern;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private static final Pattern VALID_EMAIL = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static Member of(String email, String rawPassword, PasswordEncoder passwordEncoder) {
        validateEmail(email);
        validatePassword(rawPassword);

        return new Member(normalizeEmail(email), passwordEncoder.encode(rawPassword));
    }

    public static String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT);
    }

    private static void validateEmail(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        boolean matches = VALID_EMAIL.matcher(value).matches();
        if (!matches) {
            throw new IllegalArgumentException("Email is not valid : " + value);
        }
    }

    private static void validatePassword(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (value.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member other)) {
            return false;
        }

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
