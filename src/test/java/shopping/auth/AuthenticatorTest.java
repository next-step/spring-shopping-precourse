package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.member.Member;
import shopping.member.MemberRepository;

class AuthenticatorTest {

    private static final String EMAIL = "hong@example.com";

    private final JwtTokenProvider provider =
            new JwtTokenProvider("test-secret-key-for-jwt-test-1234567890", 3_600_000);
    private final MemberRepository memberRepository = new MemberRepository();
    private final Authenticator authenticator = new Authenticator(provider, memberRepository);

    @Test
    void 유효한_토큰이면_회원을_반환한다() {
        Member saved = memberRepository.save(new Member(null, EMAIL, "1234"));
        String header = "Bearer " + provider.createToken(EMAIL);

        assertThat(authenticator.authenticate(header).getId()).isEqualTo(saved.getId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc", "Basic abc", "Bearer"})
    void 헤더_형식이_잘못되면_예외가_발생한다(String header) {
        assertThatThrownBy(() -> authenticator.authenticate(header))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 위조된_토큰이면_예외가_발생한다() {
        assertThatThrownBy(() -> authenticator.authenticate("Bearer abc.def.ghi"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void 가입되지_않은_회원의_토큰이면_예외가_발생한다() {
        String header = "Bearer " + provider.createToken(EMAIL);

        assertThatThrownBy(() -> authenticator.authenticate(header))
                .isInstanceOf(InvalidTokenException.class);
    }
}