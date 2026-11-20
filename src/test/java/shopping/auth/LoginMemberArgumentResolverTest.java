package shopping.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginMemberArgumentResolverTest {
    private static final String SECRET = "test-secret-key-must-be-at-least-32-bytes-long!!";

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 3_600_000L);
    private final LoginMemberArgumentResolver resolver =
            new LoginMemberArgumentResolver(tokenProvider);

    @Test
    void 유효한_토큰이면_회원_id를_돌려준다() {
        String token = tokenProvider.create(7L);

        Long memberId = resolver.resolveArgument(null, null, requestWith("Bearer " + token), null);

        assertThat(memberId).isEqualTo(7L);
    }

    @Test
    void 헤더가_없으면_UnauthorizedException을_던진다() {
        assertThatThrownBy(() -> resolver.resolveArgument(null, null, requestWith(null), null))
                .isInstanceOf(UnauthorizedException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Basic abc", "token-only", "Bearer ", "Bearer invalid.token.value"})
    void 잘못된_헤더면_UnauthorizedException을_던진다(String header) {
        assertThatThrownBy(() -> resolver.resolveArgument(null, null, requestWith(header), null))
                .isInstanceOf(UnauthorizedException.class);
    }

    private ServletWebRequest requestWith(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (authorization != null) {
            request.addHeader(HttpHeaders.AUTHORIZATION, authorization);
        }
        return new ServletWebRequest(request);
    }
}
