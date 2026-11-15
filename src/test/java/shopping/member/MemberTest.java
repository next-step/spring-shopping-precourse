package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"user", "user@", "user@example", "us er@example.com"})
    void 이메일_형식이_올바르지_않으면_예외가_발생한다(String email) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Member(email, "encoded"));
    }

    @Test
    void 비밀번호_일치_여부를_확인한다() {
        Member member = new Member("user@example.com", "encoded");

        assertThat(member.matchesPassword("encoded")).isTrue();
        assertThat(member.matchesPassword("other")).isFalse();
    }

    @Test
    void 토큰을_발급할_때마다_새_토큰을_만든다() {
        Member member = new Member("user@example.com", "encoded");

        assertThat(member.issueToken()).isNotEqualTo(member.issueToken());
    }
}
