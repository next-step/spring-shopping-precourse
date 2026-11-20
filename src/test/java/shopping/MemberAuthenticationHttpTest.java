package shopping;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class MemberAuthenticationHttpTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TokenRepository tokens;

    @Test
    void 가입하면_회원의_토큰을_받는다() throws IOException, InterruptedException {
        var response = post("register", "tom@example.com", "0123456789");

        Assertions.assertThat(response.statusCode()).isEqualTo(201);
        Assertions.assertThat(memberId(response)).isNotNull();
    }

    @Test
    void 같은_이메일로_다시_가입할_수_없다() throws IOException, InterruptedException {
        Assertions.assertThat(post("register", "duplicate@example.com", "0123456789").statusCode()).isEqualTo(201);

        Assertions.assertThat(post("register", "duplicate@example.com", "0123456789").statusCode()).isEqualTo(400);
    }

    @Test
    void 로그인하면_같은_회원의_토큰을_받는다() throws IOException, InterruptedException {
        var registration = post("register", "login@example.com", "0123456789");
        var login = post("login", "login@example.com", "0123456789");

        Assertions.assertThat(registration.statusCode()).isEqualTo(201);
        Assertions.assertThat(login.statusCode()).isEqualTo(200);
        Assertions.assertThat(memberId(login)).isNotNull().isEqualTo(memberId(registration));
    }

    @Test
    void 비밀번호가_틀리면_로그인할_수_없다() throws IOException, InterruptedException {
        var registration = post("register", "wrong@example.com", "0123456789");

        Assertions.assertThat(registration.statusCode()).isEqualTo(201);
        Assertions.assertThat(post("login", "wrong@example.com", "abcdefghij").statusCode())
                .isEqualTo(401);
    }

    private Long memberId(HttpResponse<String> response) {
        String token = JsonMapper.builder().build().readTree(response.body()).path("token").asString();
        return tokens.memberId(token);
    }

    private HttpResponse<String> post(String action, String email, String password)
            throws IOException, InterruptedException {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/members/" + action))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    // 이메일 형식 오류, 필드 누락 등 입력 검증도 테스트할 수 있다. 생략.
}
