package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    private static final String EMAIL = "guk@example.com";
    private static final String HASHED_PASSWORD = "hashed-password";

    @Test
    void 올바른_이메일로_회원을_만든다() {
        Member member = new Member(EMAIL, HASHED_PASSWORD);

        assertThat(member.getEmail()).isEqualTo(EMAIL);
        assertThat(member.hasPassword(HASHED_PASSWORD)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"guk", "guk@", "@example.com", "guk@example", "guk example@a.com"})
    void 이메일_형식이_잘못되면_예외(String email) {
        assertThatThrownBy(() -> new Member(email, HASHED_PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 다른_비밀번호는_일치하지_않는다() {
        Member member = new Member(EMAIL, HASHED_PASSWORD);

        assertThat(member.hasPassword("other-password")).isFalse();
    }

    @Test
    void 토큰을_발급할_때마다_새_토큰이_만들어진다() {
        Member member = new Member(EMAIL, HASHED_PASSWORD);

        String first = member.issueToken();
        String second = member.issueToken();

        assertThat(first).isNotEqualTo(second);
        assertThat(member.getToken()).isEqualTo(second);
    }
}
