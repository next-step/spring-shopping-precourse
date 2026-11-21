package shopping.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    private final Member member = new Member("user@test.com", "password");

    @Test
    @DisplayName("비밀번호가 같으면 true를 돌려준다")
    void matchesPassword() {
        assertThat(member.matchesPassword("password")).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 다르면 false를 돌려준다")
    void notMatchesPassword() {
        assertThat(member.matchesPassword("wrong-password")).isFalse();
    }
}
