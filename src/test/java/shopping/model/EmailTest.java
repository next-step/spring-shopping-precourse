package shopping.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "userexample.com", "user@", "@example.com", "user @example.com"})
    @DisplayName("이메일은 필수이며 이메일 형식이어야 한다")
    void rejectInvalidEmail(String value) {
        assertThatThrownBy(() -> new Email(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("올바른 이메일 형식이면 생성할 수 있다")
    void createValidEmail() {
        Email email = new Email("user@example.com");

        assertThat(email.value()).isEqualTo("user@example.com");
    }
}
