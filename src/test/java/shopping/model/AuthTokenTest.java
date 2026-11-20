package shopping.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class AuthTokenTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    @DisplayName("토큰 문자열은 비어 있을 수 없다")
    void rejectBlankToken(String value) {
        assertThatThrownBy(() -> new AuthToken(value, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("토큰의 회원 ID는 null일 수 없다")
    void rejectNullMemberId() {
        assertThatThrownBy(() -> new AuthToken("token", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
