package shopping.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PasswordHashTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    @DisplayName("비밀번호 해시는 비어 있을 수 없다")
    void rejectBlankPasswordHash(String value) {
        assertThatThrownBy(() -> new PasswordHash(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비어 있지 않은 비밀번호 해시는 생성할 수 있다")
    void createPasswordHash() {
        PasswordHash passwordHash = new PasswordHash("encoded-password");

        assertThat(passwordHash.value()).isEqualTo("encoded-password");
    }
}
