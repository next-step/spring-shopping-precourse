package shopping.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {
    private static final Email EMAIL = new Email("user@example.com");
    private static final PasswordHash PASSWORD_HASH = new PasswordHash("encoded-password");

    @Test
    @DisplayName("회원 이메일은 null일 수 없다")
    void rejectNullEmail() {
        assertThatThrownBy(() -> new Member(null, PASSWORD_HASH))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 비밀번호 해시는 null일 수 없다")
    void rejectNullPasswordHash() {
        assertThatThrownBy(() -> new Member(EMAIL, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 ID는 null일 수 없다")
    void rejectNullId() {
        Member member = new Member(EMAIL, PASSWORD_HASH);

        assertThatThrownBy(() -> member.assignId(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
