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
class MemberRegistrationHttpTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TokenRepository tokens;

    @Test
    void 가입하면_회원의_토큰을_받는다() throws IOException, InterruptedException {
        var response = registrationResponse("tom@example.com");

        Assertions.assertThat(response.statusCode()).isEqualTo(201);
        String token = JsonMapper.builder().build().readTree(response.body()).path("token").asText();
        Assertions.assertThat(tokens.memberId(token)).isNotNull();
    }

    @Test
    void 같은_이메일로_다시_가입할_수_없다() throws IOException, InterruptedException {
        Assertions.assertThat(registrationResponse("duplicate@example.com").statusCode()).isEqualTo(201);

        Assertions.assertThat(registrationResponse("duplicate@example.com").statusCode()).isEqualTo(400);
    }

    private HttpResponse<String> registrationResponse(String email) throws IOException, InterruptedException {
        String body = """
                {"email":"%s","password":"0123456789"}
                """.formatted(email);
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/members/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    // 이메일 형식 오류, 필드 누락 등 입력 검증도 테스트할 수 있다. 생략.
}
