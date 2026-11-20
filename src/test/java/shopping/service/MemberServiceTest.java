package shopping.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.MemberRequest;
import shopping.dto.TokenResponse;
import shopping.model.AuthToken;
import shopping.model.Member;
import shopping.repository.MemberRepository;
import shopping.repository.TokenRepository;

class MemberServiceTest {
    private static final String TOKEN = "fixed-token";

    private MemberService memberService;
    private MemberRepository memberRepository;
    private TokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private Member member;

    @BeforeEach
    void setUp() {
        memberRepository = new MemberRepository();
        tokenRepository = new TokenRepository();
        passwordEncoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        TokenService tokenService = new TokenService(new FixedTokenGenerator(), tokenRepository);
        memberService = new MemberService(memberRepository, passwordEncoder, tokenService);
        member = memberRepository.save(
                new Member("user@example.com", passwordEncoder.encode("password")));
    }

    @Test
    @DisplayName("이메일과 비밀번호가 일치하면 토큰을 발급한다")
    void issueTokenWhenLoginSucceeds() {
        TokenResponse response = memberService.login(
                new MemberRequest("user@example.com", "password"));

        assertThat(response.token()).isEqualTo(TOKEN);
        assertThat(tokenRepository.findByValue(TOKEN))
                .get()
                .extracting(AuthToken::memberId)
                .isEqualTo(member.getId());
    }

    @Test
    @DisplayName("이메일이 일치하지 않으면 토큰을 발급하지 않는다")
    void doNotIssueTokenWhenEmailDoesNotMatch() {
        MemberRequest request = new MemberRequest("other@example.com", "password");

        assertUnauthorized(request);
        assertThat(tokenRepository.findByValue(TOKEN)).isEmpty();
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 토큰을 발급하지 않는다")
    void doNotIssueTokenWhenPasswordDoesNotMatch() {
        MemberRequest request = new MemberRequest("user@example.com", "wrong-password");

        assertUnauthorized(request);
        assertThat(tokenRepository.findByValue(TOKEN)).isEmpty();
    }

    private void assertUnauthorized(MemberRequest request) {
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private static class FixedTokenGenerator extends TokenGenerator {
        @Override
        public String generate() {
            return TOKEN;
        }
    }
}
