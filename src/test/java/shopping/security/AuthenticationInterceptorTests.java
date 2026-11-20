package shopping.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import shopping.exception.InvalidTokenException;
import shopping.service.JwtProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthenticationInterceptorTests {

    private static final String TOKEN = "valid.jwt.token";

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticationInterceptor interceptor;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @DisplayName("유효한 Bearer 토큰이면 요청 속성에 회원 id를 담고 요청을 통과시킨다")
    @Test
    void validToken() throws Exception {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        given(jwtProvider.getMemberId(TOKEN)).willReturn(1L);

        boolean proceed = interceptor.preHandle(request, response, new Object());

        assertThat(proceed).isTrue();
        assertThat(request.getAttribute(AuthenticationInterceptor.MEMBER_ID_ATTRIBUTE)).isEqualTo(1L);
    }

    @DisplayName("Authorization 헤더가 없으면 InvalidTokenException이 발생하고 토큰을 검증하지 않는다")
    @Test
    void missingHeader() {
        assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(InvalidTokenException.class);
        verifyNoInteractions(jwtProvider);
    }

    @DisplayName("Bearer 형식이 아니거나 토큰이 비어 있으면 InvalidTokenException이 발생하고 토큰을 검증하지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "Basic abc123", "valid.jwt.token", "Bearer", "Bearer ", "Bearer   "})
    void malformedHeader(String header) {
        request.addHeader("Authorization", header);

        assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(InvalidTokenException.class);
        verifyNoInteractions(jwtProvider);
    }

    @DisplayName("토큰이 유효하지 않으면 InvalidTokenException이 그대로 발생하고 요청 속성을 담지 않는다")
    @Test
    void invalidToken() {
        request.addHeader("Authorization", "Bearer " + TOKEN);
        given(jwtProvider.getMemberId(TOKEN)).willThrow(new InvalidTokenException("Invalid token"));

        assertThatThrownBy(() -> interceptor.preHandle(request, response, new Object()))
                .isInstanceOf(InvalidTokenException.class);
        assertThat(request.getAttribute(AuthenticationInterceptor.MEMBER_ID_ATTRIBUTE)).isNull();
    }
}
