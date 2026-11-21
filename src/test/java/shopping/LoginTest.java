package shopping;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class LoginTest {
    private final Email email = new Email("tom", new Domain("gmail.com"));
    private final Member member = new Member(1L, email, new Password("0123456789"));
    private final Login login = new Login(
            new FakeMemberRepository(Map.of(email, member), 2L),
            new PlainPasswordEncoder()
    );

    @Test
    void 비밀번호가_일치하면_회원을_반환한다() {
        Assertions.assertThat(login.member(email, "0123456789")).isEqualTo(member);
    }

    @Test
    void 비밀번호가_틀리면_로그인할_수_없다() {
        Assertions.assertThatThrownBy(() -> login.member(email, "abcdefghij"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // 가입하지 않은 이메일로도 로그인할 수 없는지 테스트할 수 있다. 생략.
}
