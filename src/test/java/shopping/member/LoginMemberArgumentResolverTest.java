package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import shopping.ApiTest;
import shopping.common.UnauthorizedException;

class LoginMemberArgumentResolverTest extends ApiTest {

    @Autowired
    private LoginMemberArgumentResolver resolver;

    @Autowired
    private MemberService memberService;

    @Test
    void Bearer_토큰으로_로그인한_회원을_찾는다() {
        String token = memberService.register(new MemberRequest("guk@example.com", "password1")).token();

        Member member = resolver.resolveArgument(null, null, requestWith("Bearer " + token), null);

        assertThat(member.getEmail()).isEqualTo("guk@example.com");
    }

    @Test
    void Authorization_헤더가_없으면_예외() {
        NativeWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        assertThatThrownBy(() -> resolver.resolveArgument(null, null, request, null))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("인증 토큰이 필요합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Bearer wrong-token", "Basic abc"})
    void 토큰_형식이_틀리거나_없는_토큰이면_예외(String authorization) {
        assertThatThrownBy(() -> resolver.resolveArgument(null, null, requestWith(authorization), null))
                .isInstanceOf(UnauthorizedException.class);
    }

    private NativeWebRequest requestWith(String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, authorization);
        return new ServletWebRequest(request);
    }
}
