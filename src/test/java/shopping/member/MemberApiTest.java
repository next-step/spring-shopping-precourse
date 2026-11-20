package shopping.member;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static shopping.support.ApiTestSupport.assertError;
import static shopping.support.ApiTestSupport.assertInvalidInput;
import static shopping.support.ApiTestSupport.postJson;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberApiTest {

    static final String SIGNUP_BODY = """
                {"email": "Kim@example.com", "password": "password1234567", "name": "김"}
            """;

    static final String REGISTER_URL = "/api/members/register";
    static final String LOGIN_URL = "/api/members/login";

    @Autowired
    MockMvcTester mvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    Long memberIdOf(MvcTestResult result) {
        String body = new String(result.getResponse().getContentAsByteArray(),
                StandardCharsets.UTF_8);
        String token = JsonPath.read(body, "$.token");
        return jwtProvider.getMemberId(token);
    }

    Member findSignedUpMember() {
        return memberRepository.findByEmail("kim@example.com").orElseThrow();
    }

    @Test
    void 가입하면_201과_회원_id를_sub에_담은_토큰을_준다() {
        // when
        MvcTestResult result = postJson(mvc, REGISTER_URL, SIGNUP_BODY);

        // then
        assertThat(result).hasStatus(CREATED);
        Member member = findSignedUpMember();
        assertThat(memberIdOf(result)).isEqualTo(member.getId());
    }

    @Test
    void 이메일은_소문자로_비밀번호는_해시로_저장한다() {
        // when
        postJson(mvc, REGISTER_URL, SIGNUP_BODY);

        // then
        Member member = findSignedUpMember();
        assertThat(member.getPasswordHash()).isNotEqualTo("password1234567");
        assertThat(passwordEncoder.matches("password1234567", member.getPasswordHash())).isTrue();
    }

    @Test
    void 대소문자만_다른_이메일로_다시_가입하면_DUPLICATE_EMAIL이다() {
        // given
        postJson(mvc, REGISTER_URL, SIGNUP_BODY);

        // when
        MvcTestResult result = postJson(mvc, REGISTER_URL, """
                    {"email": "kim@example.com", "password": "password1234567", "name": "김"}
                """);

        // then
        assertError(result, CONFLICT, "DUPLICATE_EMAIL");
    }

    @Test
    void 가입_값이_없으면_필드마다_누락_코드를_준다() {
        // when
        MvcTestResult result = postJson(mvc, REGISTER_URL, "{}");

        // then
        assertInvalidInput(result, "EMAIL_MISSING", "PASSWORD_MISSING",
                "NAME_MISSING");
    }

    @Test
    void 가입_값의_형식이_틀리면_어긴_규칙의_코드를_준다() {
        // when
        MvcTestResult result = postJson(mvc, REGISTER_URL, """
                    {"email": "a@b", "password": "short", "name": "%s"}
                """.formatted("가".repeat(40)));

        // then
        assertInvalidInput(result, "INVALID_EMAIL_FORMAT", "INVALID_PASSWORD_LENGTH",
                "NAME_TOO_LONG");
    }

    @Test
    void 비밀번호에_공백이_있으면_INVALID_PASSWORD_CHARACTER이다() {
        // when
        MvcTestResult result = postJson(mvc, REGISTER_URL, """
                    {"email": "kim@example.com", "password": "pass word1234567", "name": "김"}
                """);

        // then
        assertInvalidInput(result, "INVALID_PASSWORD_CHARACTER");
    }

    @Test
    void 로그인하면_200과_회원_id를_sub에_담은_토큰을_준다() {
        // given
        postJson(mvc, REGISTER_URL, SIGNUP_BODY);

        // when
        MvcTestResult result = postJson(mvc, LOGIN_URL, """
                    {"email": "KIM@example.com", "password": "password1234567"}
                """);

        // then
        assertThat(result).hasStatus(OK);
        Member member = findSignedUpMember();
        assertThat(memberIdOf(result)).isEqualTo(member.getId());
    }

    @Test
    void 비밀번호가_틀리면_FAIL_LOGIN이다() {
        // given
        postJson(mvc, REGISTER_URL, SIGNUP_BODY);

        // when
        MvcTestResult result = postJson(mvc, LOGIN_URL, """
                    {"email": "kim@example.com", "password": "wrongpassword123456"}
                """);

        // then
        assertError(result, UNAUTHORIZED, "FAIL_LOGIN");
    }

    @Test
    void 가입하지_않은_이메일도_같은_FAIL_LOGIN이다() {
        // when
        MvcTestResult result = postJson(mvc, LOGIN_URL, """
                    {"email": "nobody@example.com", "password": "password1234567"}
                """);

        // then
        assertError(result, UNAUTHORIZED, "FAIL_LOGIN");
        assertThat(result).bodyJson().extractingPath("$.detail")
                .isEqualTo("이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void 로그인_값이_없으면_필드마다_누락_코드를_준다() {
        // when
        MvcTestResult result = postJson(mvc, LOGIN_URL, "{}");

        // then
        assertInvalidInput(result, "EMAIL_MISSING", "PASSWORD_MISSING");
    }
}
