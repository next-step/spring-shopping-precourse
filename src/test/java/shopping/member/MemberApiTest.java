package shopping.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import shopping.support.ApiTest;

class MemberApiTest extends ApiTest {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "password123";

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원_가입을_하면_토큰을_받는다() {
        MvcTestResult result = post("/api/members/register", memberJson(EMAIL, PASSWORD));

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.token").asString().isNotBlank();
    }

    @Test
    void 비밀번호는_평문으로_저장하지_않는다() {
        post("/api/members/register", memberJson(EMAIL, PASSWORD));

        Member member = memberRepository.findByEmail(EMAIL).orElseThrow();

        assertThat(member.getPassword()).isNotEqualTo(PASSWORD);
    }

    @Test
    void 이미_가입된_이메일로는_가입할_수_없다() {
        post("/api/members/register", memberJson(EMAIL, PASSWORD));

        MvcTestResult result = post("/api/members/register", memberJson(EMAIL, "other-password"));

        assertThat(result).hasStatus(HttpStatus.CONFLICT);
        assertThat(result).bodyJson().extractingPath("$.message").isEqualTo("이미 가입된 이메일입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "not-an-email", "user@"})
    void 이메일_형식이_올바르지_않으면_가입할_수_없다(String email) {
        MvcTestResult result = post("/api/members/register", memberJson(email, PASSWORD));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 비밀번호가_비어_있으면_가입할_수_없다() {
        MvcTestResult result = post("/api/members/register", memberJson(EMAIL, ""));

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message")
                .isEqualTo("비밀번호는 비어 있을 수 없습니다.");
    }

    @Test
    void 로그인하면_토큰을_받는다() {
        post("/api/members/register", memberJson(EMAIL, PASSWORD));

        MvcTestResult result = post("/api/members/login", memberJson(EMAIL, PASSWORD));

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.token").asString().isNotBlank();
    }

    @Test
    void 비밀번호가_틀리면_로그인에_실패한다() {
        post("/api/members/register", memberJson(EMAIL, PASSWORD));

        MvcTestResult result = post("/api/members/login", memberJson(EMAIL, "wrong-password"));

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void 가입하지_않은_이메일로는_로그인에_실패한다() {
        MvcTestResult result = post("/api/members/login", memberJson(EMAIL, PASSWORD));

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
    }
}
