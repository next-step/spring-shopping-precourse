package shopping.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Objects;

@Embeddable
public class Email {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();
    private static final int MAX_LENGTH = 254;

    @Column(name = "email", nullable = false, length = MAX_LENGTH)
    @jakarta.validation.constraints.Email
    private String value;

    protected Email() {
    }

    public Email(String value) {
        if (value == null) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        String normalized = value.strip();
        validate(normalized);
        this.value = normalized;
    }

    private static void validate(String value) {
        if (value.isEmpty() || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("이메일은 1~254자여야 합니다.");
        }
        if (value.codePoints().anyMatch(Email::isSpace)
                || !VALIDATOR.validateValue(Email.class, "value", value).isEmpty()) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
    }

    private static boolean isSpace(int character) {
        return Character.isWhitespace(character) || Character.isSpaceChar(character);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Email email && Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
