package shopping.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopping.domain.Members;
import shopping.service.MemberService;
import shopping.service.TokenStore;

class MemberControllerTest {
    private static final String EMAIL = "user@gmail.com";
    private static final String PASSWORD = "abcd123!";

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        MemberService service = new MemberService(new Members(), new TokenStore());
        mvc = MockMvcBuilders.standaloneSetup(new MemberController(service))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private ResultActions send(String path, String json) throws Exception {
        return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions register(String email, String password) throws Exception {
        return send("/api/members/register", json(email, password));
    }

    private ResultActions login(String email, String password) throws Exception {
        return send("/api/members/login", json(email, password));
    }

    private static String json(String email, String password) {
        return "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    }

    @Test
    @DisplayName("가입하면 201과 토큰을 돌려주고 비밀번호는 응답에 없다")
    void registerMember() throws Exception {
        MvcResult result = register(EMAIL, PASSWORD)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThat(body).doesNotContain(PASSWORD).doesNotContain("password");
        String token = body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
        assertThat(UUID.fromString(token)).hasToString(token);
    }

    @Test
    @DisplayName("같은 이메일로 다시 가입하면 400을 돌려준다")
    void registerDuplicate() throws Exception {
        register(EMAIL, PASSWORD).andExpect(status().isCreated());

        register(EMAIL, "zzzz9999!")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "user@naver.com", "user@@gmail.com", ""})
    @DisplayName("이메일 규칙을 어기면 가입할 때 400을 돌려준다")
    void registerInvalidEmail(String email) throws Exception {
        register(email, PASSWORD)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"short1!", "abcdefgh!", "12345678!", "abcd1234", ""})
    @DisplayName("비밀번호 규칙을 어기면 가입할 때 400을 돌려준다")
    void registerInvalidPassword(String password) throws Exception {
        register(EMAIL, password)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"email\":\"user@gmail.com\"}",
            "{\"password\":\"abcd123!\"}", "{\"email\":null,\"password\":null}",
            "", "not json", "[]"})
    @DisplayName("필수 값이 없거나 JSON이 올바르지 않으면 가입과 로그인 모두 400을 돌려준다")
    void missingOrMalformed(String json) throws Exception {
        send("/api/members/register", json).andExpect(status().isBadRequest());
        send("/api/members/login", json).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일과 비밀번호가 맞으면 200과 토큰을 돌려준다")
    void loginMember() throws Exception {
        register(EMAIL, PASSWORD);

        login(EMAIL, PASSWORD)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"zzzz9999!", "ABCD123!", "short", ""})
    @DisplayName("비밀번호가 틀리면 401을 돌려준다")
    void loginWrongPassword(String password) throws Exception {
        register(EMAIL, PASSWORD);

        login(EMAIL, password)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"none@gmail.com", "user", "user@naver.com", ""})
    @DisplayName("가입하지 않았거나 형식이 틀린 이메일이면 401을 돌려준다")
    void loginUnknownEmail(String email) throws Exception {
        register(EMAIL, PASSWORD);

        login(email, PASSWORD)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }
}
