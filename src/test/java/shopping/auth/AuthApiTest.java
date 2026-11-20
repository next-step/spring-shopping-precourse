package shopping.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static shopping.support.ApiTestSupport.assertError;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthApiTest.TestController.class)
class AuthApiTest {

    static final String AUTH_TEST_URL = "/api/wishes/auth-test";

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    String secret;

    MvcTestResult getWithAuthorization(String authorization) {
        return mvc.get().uri(AUTH_TEST_URL)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .exchange();
    }

    @Test
    void 올바른_토큰이면_컨트롤러가_회원_id를_받는다() {
        // given
        String token = jwtProvider.createToken(7L);

        // when
        MvcTestResult result = getWithAuthorization("Bearer " + token);

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).hasBodyTextEqualTo("7");
    }

    @Test
    void Authorization_헤더가_없으면_INVALID_TOKEN이다() {
        // when
        MvcTestResult result = mvc.get().uri(AUTH_TEST_URL).exchange();

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void Bearer로_시작하지_않으면_INVALID_TOKEN이다() {
        // given
        String token = jwtProvider.createToken(7L);

        // when
        MvcTestResult result = getWithAuthorization(token);

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void JWT_형식이_아니면_INVALID_TOKEN이다() {
        // when
        MvcTestResult result = getWithAuthorization("Bearer abc");

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void 다른_키로_서명한_토큰이면_INVALID_TOKEN이다() {
        // given
        String token = new JwtProvider("x".repeat(32), 30).createToken(7L);

        // when
        MvcTestResult result = getWithAuthorization("Bearer " + token);

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void 만료된_토큰이면_INVALID_TOKEN이다() {
        // given
        String token = new JwtProvider(secret, -1).createToken(7L);

        // when
        MvcTestResult result = getWithAuthorization("Bearer " + token);

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void 인터셉터를_거치지_않은_경로에서_LoginMemberId를_쓰면_INVALID_TOKEN이다() {
        // when
        MvcTestResult result = mvc.get().uri("/test/no-interceptor")
                .exchange();

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @RestController
    static class TestController {

        @GetMapping(AUTH_TEST_URL)
        public Long memberId(@LoginMemberId Long memberId) {
            return memberId;
        }

        @GetMapping("/test/no-interceptor")
        public Long noInterceptor(@LoginMemberId Long memberId) {
            return memberId;
        }
    }
}
