package shopping.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import shopping.domain.ProfanityCheckException;
import shopping.domain.ProfanityCheckException.Reason;

class PurgoMalumProfanityCheckerTest {
    private static final String BASE_URL = "https://www.purgomalum.com";
    private static final String PATH = BASE_URL + "/service/containsprofanity";

    private MockRestServiceServer server;
    private PurgoMalumProfanityChecker checker;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        checker = new PurgoMalumProfanityChecker(builder.build());
    }

    @Test
    @DisplayName("200 응답의 본문이 true이면 비속어가 있다")
    void containsProfanity() {
        server.expect(requestTo(PATH + "?text=shit"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("shit")).isTrue();
        server.verify();
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "a&b|a%26b",
            "a+b|a%2Bb",
            "a/b|a%2Fb",
            "a b|a%20b",
            "a_b-c|a_b-c",
            "(a)[b]|%28a%29%5Bb%5D"})
    @DisplayName("이름의 특수문자는 쿼리 값으로 인코딩해 전달한다")
    void encodeText(String text, String encoded) {
        server.expect(requestTo(PATH + "?text=" + encoded))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        checker.containsProfanity(text);

        server.verify();
    }

    @Test
    @DisplayName("200 응답의 본문이 false이면 비속어가 없다")
    void doesNotContainProfanity() {
        server.expect(requestTo(PATH + "?text=hello"))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("hello")).isFalse();
        server.verify();
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 405, 429})
    @DisplayName("4xx 응답이면 요청 오류(BAD_REQUEST)로 처리한다")
    void clientError(int status) {
        server.expect(requestTo(PATH + "?text=hello"))
                .andRespond(withStatus(HttpStatus.valueOf(status)));

        assertThatThrownBy(() -> checker.containsProfanity("hello"))
                .isInstanceOfSatisfying(ProfanityCheckException.class,
                        e -> assertThat(e.reason()).isEqualTo(Reason.BAD_REQUEST));
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 503, 504})
    @DisplayName("5xx 응답이면 서비스 오류(SERVICE_ERROR)로 처리한다")
    void serverError(int status) {
        server.expect(requestTo(PATH + "?text=hello"))
                .andRespond(withStatus(HttpStatus.valueOf(status)));

        assertThatThrownBy(() -> checker.containsProfanity("hello"))
                .isInstanceOfSatisfying(ProfanityCheckException.class,
                        e -> assertThat(e.reason()).isEqualTo(Reason.SERVICE_ERROR));
    }

    static Stream<IOException> connectionFailures() {
        return Stream.of(
                new SocketTimeoutException("read timed out"),
                new ConnectException("connection refused"),
                new UnknownHostException("www.purgomalum.com"));
    }

    @ParameterizedTest
    @MethodSource("connectionFailures")
    @DisplayName("타임아웃, 연결 실패는 연결 오류(CONNECTION_ERROR)로 처리한다")
    void connectionError(IOException failure) {
        server.expect(requestTo(PATH + "?text=hello"))
                .andRespond(withException(failure));

        assertThatThrownBy(() -> checker.containsProfanity("hello"))
                .isInstanceOfSatisfying(ProfanityCheckException.class,
                        e -> assertThat(e.reason()).isEqualTo(Reason.CONNECTION_ERROR));
    }

    @ParameterizedTest
    @ValueSource(strings = {"maybe", "", "yes", "1", "True", "<html>error</html>"})
    @DisplayName("200인데 본문이 true/false가 아니면 응답 형식 오류(INVALID_RESPONSE)로 처리한다")
    void invalidBody(String body) {
        server.expect(requestTo(PATH + "?text=hello"))
                .andRespond(withSuccess(body, MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> checker.containsProfanity("hello"))
                .isInstanceOfSatisfying(ProfanityCheckException.class,
                        e -> assertThat(e.reason()).isEqualTo(Reason.INVALID_RESPONSE));
    }

    @ParameterizedTest
    @ValueSource(ints = {204, 302})
    @DisplayName("본문이 없는 2xx/3xx 응답도 응답 형식 오류(INVALID_RESPONSE)로 처리한다")
    void noBody(int status) {
        server.expect(requestTo(PATH + "?text=hello"))
                .andRespond(withStatus(HttpStatus.valueOf(status)));

        assertThatThrownBy(() -> checker.containsProfanity("hello"))
                .isInstanceOfSatisfying(ProfanityCheckException.class,
                        e -> assertThat(e.reason()).isEqualTo(Reason.INVALID_RESPONSE));
    }
}
