package shopping.member.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("비밀번호")
class PasswordTest {

    @DisplayName("비밀번호 길이는 5자와 16자를 허용한다")
    @ParameterizedTest
    @ValueSource(ints = {5, 16})
    void acceptsBoundaryLengths(int length) {
        assertThatCode(() -> new Password("a".repeat(length))).doesNotThrowAnyException();
    }

    @DisplayName("누락되거나 길이가 맞지 않거나 공백이 있는 비밀번호는 거절한다")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abcd", "12345678901234567", " abcd", "ab cd", "abcd\t"})
    void rejectsMissingOutOfRangeOrWhitespacePassword(String value) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Password(value));
    }
}
