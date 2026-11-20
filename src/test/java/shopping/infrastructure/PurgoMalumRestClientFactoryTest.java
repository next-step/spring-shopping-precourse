package shopping.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import shopping.domain.ProfanityCheckException;
import shopping.domain.ProfanityCheckException.Reason;

class PurgoMalumRestClientFactoryTest {
    private static final Duration TIMEOUT = Duration.ofMillis(200);
    private static final long SLOW_MILLIS = 1500;

    private HttpServer server;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    @DisplayName("설정한 읽기 시간 안에 응답이 없으면 연결 오류(CONNECTION_ERROR)가 발생한다")
    void readTimeout() {
        server.createContext("/service/containsprofanity", exchange -> {
            sleep(SLOW_MILLIS);
            exchange.close();
        });
        server.start();
        RestClient client = PurgoMalumRestClientFactory.create(baseUrl, TIMEOUT, TIMEOUT);
        PurgoMalumProfanityChecker checker = new PurgoMalumProfanityChecker(client);

        assertThatThrownBy(() -> checker.containsProfanity("hello"))
                .isInstanceOfSatisfying(ProfanityCheckException.class,
                        e -> assertThat(e.reason()).isEqualTo(Reason.CONNECTION_ERROR));
    }

    @Test
    @DisplayName("제한 시간 안에 응답하면 정상 처리한다")
    void respondsInTime() {
        server.createContext("/service/containsprofanity", exchange -> {
            byte[] body = "true".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        RestClient client = PurgoMalumRestClientFactory.create(baseUrl, TIMEOUT, TIMEOUT);

        assertThat(new PurgoMalumProfanityChecker(client).containsProfanity("shit")).isTrue();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
