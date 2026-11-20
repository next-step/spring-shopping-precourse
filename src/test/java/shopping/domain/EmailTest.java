package shopping.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "user@gmail.com",
            "a.b+c@gmail.com",
            "USER123@gmail.com",
            "user@GMAIL.COM",
            "user@Gmail.Com"})
    @DisplayName("gmail.com 이메일은 생성할 수 있다")
    void createValid(String value) {
        Email email = new Email(value);

        assertThat(email.value()).isEqualTo(value);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("null, 빈 값, 공백만 있는 이메일은 예외가 발생한다")
    void createBlank(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"usergmail.com", "user", "user.gmail.com"})
    @DisplayName("@가 없으면 예외가 발생한다")
    void createWithoutAt(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@@gmail.com", "a@b@gmail.com", "user@gmail.com@gmail.com"})
    @DisplayName("@가 둘 이상이면 예외가 발생한다")
    void createWithManyAt(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"@gmail.com", "user@", "@"})
    @DisplayName("@ 앞이나 뒤가 비어 있으면 예외가 발생한다")
    void createWithEmptyPart(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@naver.com",
            "user@kakao.com",
            "user@gmail.co",
            "user@gmail",
            "user@gmail.com.kr",
            "user@sub.gmail.com",
            "user@mygmail.com",
            "user@gmail.comm"})
    @DisplayName("gmail.com이 아닌 도메인은 예외가 발생한다")
    void createWithOtherDomain(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " user@gmail.com",
            "user@gmail.com ",
            "us er@gmail.com",
            "user@gmail.com\n",
            "user\t@gmail.com"})
    @DisplayName("공백 문자가 있으면 예외가 발생한다")
    void createWithWhitespace(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("값이 같으면 같은 이메일이다")
    void equality() {
        Email email = new Email("user@gmail.com");

        assertThat(email).isEqualTo(new Email("user@gmail.com"));
        assertThat(email).hasSameHashCodeAs(new Email("user@gmail.com"));
        assertThat(email).isNotEqualTo(new Email("other@gmail.com"));
    }
}
