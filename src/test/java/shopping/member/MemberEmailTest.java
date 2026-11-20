package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberEmailTest {

    @Test
    @DisplayName("정상 이메일은 만들어진다")
    void createsValidEmail() {
        MemberEmail email = new MemberEmail("test@test.com");
        assertThat(email.getValue()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("빈 이메일이면 예외가 발생한다")
    void throwsWhenBlank() {
        assertThatThrownBy(() -> new MemberEmail(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("@가 없으면 예외가 발생한다")
    void throwsWhenNoAt() {
        assertThatThrownBy(() -> new MemberEmail("testtest.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("@ 뒤에 점이 없으면 예외가 발생한다")
    void throwsWhenNoDot() {
        assertThatThrownBy(() -> new MemberEmail("test@test"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}