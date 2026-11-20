package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.exception.ConflictException;
import shopping.exception.UnauthorizedException;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void 회원가입하면_회원을_식별하는_토큰을_발급한다() {
        String token = memberService.register("a@test.com", "1234");

        assertThat(jwtProvider.getMemberId(token)).isNotNull();
    }

    @Test
    void 이미_가입된_이메일로_가입하면_예외가_발생한다() {
        memberService.register("a@test.com", "1234");

        assertThatThrownBy(() -> memberService.register("a@test.com", "5678"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void 로그인에_성공하면_토큰을_발급한다() {
        memberService.register("a@test.com", "1234");

        String token = memberService.login("a@test.com", "1234");

        assertThat(token).isNotBlank();
    }

    @Test
    void 비밀번호가_틀리면_로그인에_실패한다() {
        memberService.register("a@test.com", "1234");

        assertThatThrownBy(() -> memberService.login("a@test.com", "wrong"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 위조된_토큰은_검증에_실패한다() {
        assertThatThrownBy(() -> jwtProvider.getMemberId("fake.token.value"))
                .isInstanceOf(UnauthorizedException.class);
    }
}