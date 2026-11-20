package shopping.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import shopping.service.MemberService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class MemberControllerTest {
    private final MockMvcTester mvc = MockMvcTester.of(new MemberController(new MemberService()));

    @ParameterizedTest
    @ValueSource(strings = {"/api/members/register", "/api/members/login"})
    void 비밀번호가_공백뿐이면_400을_반환한다(String path) {
        String body = """
                {"email":"reader@example.com","password":"   "}
                """;

        assertThat(mvc.post().uri(path).contentType(APPLICATION_JSON).content(body))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }
}