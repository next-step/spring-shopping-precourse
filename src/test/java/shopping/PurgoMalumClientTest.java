package shopping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.SocketTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PurgoMalumClientTest {

    private MockRestServiceServer server;
    private PurgoMalumClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://www.purgomalum.com");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new PurgoMalumClient(builder.build());
    }

    @AfterEach
    void verifyRequests() {
        server.verify();
    }

    @ParameterizedTest
    @CsvSource({"true, true", "false, false"})
    void returnsProfanityResult(String response, boolean expected) {
        server.expect(
                        requestTo(
                                "https://www.purgomalum.com/service/containsprofanity?text=keyboard"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(response, MediaType.TEXT_PLAIN));

        assertThat(client.containsProfanity("keyboard")).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "unexpected", "{\"error\":\"failed\"}"})
    void rejectsUnexpectedResponse(String response) {
        server.expect(
                        requestTo(
                                "https://www.purgomalum.com/service/containsprofanity?text=keyboard"))
                .andRespond(withSuccess(response, MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> client.containsProfanity("keyboard"))
                .isInstanceOf(ProfanityServiceUnavailableException.class);
    }

    @Test
    void convertsHttpErrorToUnavailableException() {
        server.expect(
                        requestTo(
                                "https://www.purgomalum.com/service/containsprofanity?text=keyboard"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));

        assertThatThrownBy(() -> client.containsProfanity("keyboard"))
                .isInstanceOf(ProfanityServiceUnavailableException.class);
    }

    @Test
    void convertsTimeoutToUnavailableException() {
        server.expect(
                        requestTo(
                                "https://www.purgomalum.com/service/containsprofanity?text=keyboard"))
                .andRespond(withException(new SocketTimeoutException("timeout")));

        assertThatThrownBy(() -> client.containsProfanity("keyboard"))
                .isInstanceOf(ProfanityServiceUnavailableException.class);
    }
}
