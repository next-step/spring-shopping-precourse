package shopping.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import shopping.common.exception.UnauthorizedException;

class LoginMemberArgumentResolverTest {

    private static final String SECRET = "test-secret-key-test-secret-key-test-secret-key";
    private static final long ONE_HOUR = 3_600_000L;

    private final TokenProvider tokenProvider = new JwtTokenProvider(SECRET, ONE_HOUR);
    private final LoginMemberArgumentResolver resolver = new LoginMemberArgumentResolver(tokenProvider);

    @Test
    void 올바른_토큰이면_회원_id를_꺼낸다() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.createToken(1L));

        Object memberId = resolver.resolveArgument(null, null, new ServletWebRequest(request), null);

        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    void 토큰_헤더가_없으면_예외가_발생한다() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertThatThrownBy(() -> resolver.resolveArgument(null, null, new ServletWebRequest(request), null))
                .isInstanceOf(UnauthorizedException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "Basic abc", "Bearer ", "Bearer abc"})
    void 헤더_형식이나_토큰이_잘못되면_예외가_발생한다(String header) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, header);

        assertThatThrownBy(() -> resolver.resolveArgument(null, null, new ServletWebRequest(request), null))
                .isInstanceOf(UnauthorizedException.class);
    }
}
