package shopping.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.function.IntPredicate;

public class Password {
    private static final int MIN_LENGTH = 8;
    private static final String SPECIAL_CHARACTERS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private final String value;

    public Password(String value) {
        validateNotEmpty(value);
        validateLength(value);
        validateAllowedCharacters(value);
        validateContains(value, Password::isLetter, "비밀번호에는 영문이 하나 이상 있어야 합니다.");
        validateContains(value, Password::isDigit, "비밀번호에는 숫자가 하나 이상 있어야 합니다.");
        validateContains(value, Password::isSpecial,
                "비밀번호에는 특수문자가 하나 이상 있어야 합니다.");
        this.value = value;
    }

    private void validateNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해 주세요.");
        }
    }

    private void validateLength(String value) {
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
    }

    private void validateAllowedCharacters(String value) {
        boolean allowed = value.chars().allMatch(
                character -> isLetter(character) || isDigit(character) || isSpecial(character));
        if (!allowed) {
            throw new IllegalArgumentException("비밀번호에는 영문, 숫자, 특수문자(!, @ 등)만 사용할 수 있습니다. "
                    + "띄어쓰기와 한글은 사용할 수 없습니다.");
        }
    }

    private void validateContains(String value, IntPredicate kind, String message) {
        if (value.chars().noneMatch(kind)) {
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean isLetter(int character) {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }

    private static boolean isDigit(int character) {
        return character >= '0' && character <= '9';
    }

    private static boolean isSpecial(int character) {
        return SPECIAL_CHARACTERS.indexOf(character) >= 0;
    }

    // 비밀번호를 꺼내 비교하지 않고, 비밀번호가 스스로 같은지 알려준다. 비교 시간으로 값을 추측하지 못하게 한다.
    public boolean matches(Password other) {
        validateNotNull(other, "비교할 비밀번호가 없습니다.");
        byte[] mine = value.getBytes(StandardCharsets.UTF_8);
        byte[] theirs = other.value.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(mine, theirs);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof Password password && value.equals(password.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "****";
    }

    private static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new NullPointerException(message);
        }
    }
}
