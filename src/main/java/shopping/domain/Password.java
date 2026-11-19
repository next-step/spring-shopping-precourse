package shopping.domain;

import java.util.function.IntPredicate;

public class Password {
    private static final int MIN_LENGTH = 8;
    private static final String SPECIAL_CHARACTERS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private final String value;

    public Password(String value) {
        validateNotEmpty(value);
        validateLength(value);
        validateAllowedCharacters(value);
        validateContains(value, Password::isLetter, "영문");
        validateContains(value, Password::isDigit, "숫자");
        validateContains(value, Password::isSpecial, "특수문자");
        this.value = value;
    }

    private void validateNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
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
            throw new IllegalArgumentException("비밀번호에는 영문, 숫자, 특수문자만 사용할 수 있습니다.");
        }
    }

    private void validateContains(String value, IntPredicate kind,
            String kindName) {
        if (value.chars().noneMatch(kind)) {
            throw new IllegalArgumentException("비밀번호에는 " + kindName + "가 포함되어야 합니다.");
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
}
