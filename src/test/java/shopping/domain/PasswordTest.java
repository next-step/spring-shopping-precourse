package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "abcd123!",
            "Abc12345@",
            "a1!aaaaa",
            "1!aaaaaa",
            "!1aaaaaa",
            "Passw0rd#2024",
            "~`!@#$%^&*()-_=+[]{}\\|;:'\",.<>/?a1"})
    @DisplayName("영문, 숫자, 특수문자를 포함한 8자 이상은 생성할 수 있다")
    void createValid(String value) {
        Password password = new Password(value);

        assertThat(password.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("길이 상한은 없다")
    void createVeryLong() {
        String value = "a1!" + "x".repeat(500);

        assertThat(new Password(value).value()).isEqualTo(value);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("null과 빈 값은 예외가 발생한다")
    void createBlank(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a1!", "abc12!x", "a1!aaaa"})
    @DisplayName("8자 미만이면 예외가 발생한다")
    void createTooShort(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678!", "!!!!1111"})
    @DisplayName("영문이 없으면 예외가 발생한다")
    void createWithoutLetter(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcdefg!", "ABCDEFG!"})
    @DisplayName("숫자가 없으면 예외가 발생한다")
    void createWithoutDigit(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abcd1234", "ABCD1234", "a1b2c3d4e5"})
    @DisplayName("특수문자가 없으면 예외가 발생한다")
    void createWithoutSpecial(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "abcd 123!",
            " abcd123!",
            "abcd123! ",
            "abcd123!가",
            "비밀번호abcd1!",
            "abcd123é!",
            "abcd123!😀",
            "abcd\t123!"})
    @DisplayName("영문, 숫자, ASCII 특수문자 외의 문자가 있으면 예외가 발생한다")
    void createWithNotAllowedCharacter(String value) {
        assertThatThrownBy(() -> new Password(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("toString은 비밀번호를 노출하지 않는다")
    void toStringHidesValue() {
        Password password = new Password("abcd123!");

        assertThat(password.toString()).doesNotContain("abcd123!");
    }

    @Test
    @DisplayName("값이 같으면 같은 비밀번호다")
    void equality() {
        Password password = new Password("abcd123!");

        assertThat(password).isEqualTo(new Password("abcd123!"));
        assertThat(password).hasSameHashCodeAs(new Password("abcd123!"));
        assertThat(password).isNotEqualTo(new Password("abcd123@"));
    }

    @Test
    @DisplayName("입력한 비밀번호가 같은지 스스로 확인하고, 대소문자를 구분한다")
    void matches() {
        Password password = new Password("Abcd123!");

        assertThat(password.matches(new Password("Abcd123!"))).isTrue();
        assertThat(password.matches(new Password("abcd123!"))).isFalse();
        assertThat(password.matches(new Password("Abcd123@"))).isFalse();
    }

    @Test
    @DisplayName("null과는 비교할 수 없다")
    void matchesNull() {
        assertThatThrownBy(() -> new Password("abcd123!").matches(null))
                .isInstanceOf(NullPointerException.class);
    }
}
