package shopping.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Password {

    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 16;

    @Column(name = "password", nullable = false, length = MAX_LENGTH)
    private String value;

    protected Password() {
    }

    public Password(String value) {
        if (value == null || value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 5~16자여야 합니다.");
        }
        if (value.codePoints().anyMatch(Password::isSpace)) {
            throw new IllegalArgumentException("비밀번호에는 공백을 사용할 수 없습니다.");
        }

        this.value = value;
    }

    public boolean matches(Password candidate) {
        return value.equals(candidate.value);
    }

    private static boolean isSpace(int character) {
        return Character.isWhitespace(character) || Character.isSpaceChar(character);
    }
}
