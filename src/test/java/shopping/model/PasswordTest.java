package shopping.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordTest {
    @Test
    @DisplayName("비밀번호는 null일 수 없다")
    void rejectNullPassword() {
        assertThatThrownBy(() -> new Password(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
