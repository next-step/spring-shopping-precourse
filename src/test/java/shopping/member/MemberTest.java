package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @Test
    void 정상_값으로_회원이_생성된다() {
        Member member = new Member(null, "hong@example.com", "1234");

        assertThat(member.getEmail()).isEqualTo("hong@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "a@b", "a b@example.com", "@example.com"})
    void 이메일_형식이_올바르지_않으면_예외가_발생한다(String email) {
        assertThatThrownBy(() -> new Member(null, email, "1234"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void 비밀번호가_비어_있으면_예외가_발생한다(String password) {
        assertThatThrownBy(() -> new Member(null, "hong@example.com", password))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비밀번호_일치_여부를_확인한다() {
        Member member = new Member(null, "hong@example.com", "1234");

        assertThat(member.matchesPassword("1234")).isTrue();
        assertThat(member.matchesPassword("0000")).isFalse();
    }
}