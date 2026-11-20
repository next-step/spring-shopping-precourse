package shopping.product.client;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurgoMalumClientTest {

    private static final Duration TIMEOUT = Duration.ofMillis(500);

    private HttpServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.setExecutor(Executors.newFixedThreadPool(2));
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void 비속어가_있다고_응답하면_true다() {
        // given
        respond("true", 0);

        // when
        boolean result = client().containsProfanity("anything");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 응답이_시간_제한보다_늦으면_FAIL_CALL_PURGOMALUM이다() {
        // given
        respond("false", 2_000);

        // when & then
        assertThatThrownBy(() -> client().containsProfanity("hello"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FAIL_CALL_PURGOMALUM);
    }

    void respond(String body, long delayMillis) {
        server.createContext("/service/containsprofanity", exchange -> {
            sleep(delayMillis);
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        });
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    PurgoMalumClient client() {
        String baseUrl = "http://localhost:" + server.getAddress().getPort();
        return new PurgoMalumClient(baseUrl, TIMEOUT, TIMEOUT);
    }
}
