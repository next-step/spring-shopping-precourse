package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberPasswordTest {

    @Test
    @DisplayName("정상 비밀번호는 만들어진다")
    void createsValidPassword() {
        MemberPassword password = new MemberPassword("1234");
        assertThat(password.getValue()).isEqualTo("1234");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("비밀번호가 비어 있으면 예외가 발생한다")
    void throwsWhenBlank(String value) {
        assertThatThrownBy(() -> new MemberPassword(value))
                .isInstanceOf(IllegalArgumentException.class);
    }
}