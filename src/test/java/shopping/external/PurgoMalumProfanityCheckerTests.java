package shopping.external;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(
        value = PurgoMalumProfanityChecker.class,
        properties = "purgomalum.base-url=https://www.purgomalum.com"
)
class PurgoMalumProfanityCheckerTests {

    private static final String BASE_URL = "https://www.purgomalum.com/service/containsprofanity";

    @Autowired
    private PurgoMalumProfanityChecker profanityChecker;

    @Autowired
    private MockRestServiceServer server;

    @DisplayName("응답이 true이면 비속어가 포함된 것으로 판단한다")
    @Test
    void containsProfanity() {
        server.expect(requestTo(BASE_URL + "?text=fuck"))
                .andExpect(method(GET))
                .andRespond(withSuccess("true", TEXT_PLAIN));

        assertThat(profanityChecker.containsProfanity("fuck")).isTrue();
        server.verify();
    }

    @DisplayName("응답이 false이면 비속어가 포함되지 않은 것으로 판단한다")
    @Test
    void doesNotContainProfanity() {
        server.expect(requestTo(BASE_URL + "?text=apple"))
                .andRespond(withSuccess("false", TEXT_PLAIN));

        assertThat(profanityChecker.containsProfanity("apple")).isFalse();
        server.verify();
    }

    @DisplayName("공백과 특수문자는 인코딩하여 요청한다")
    @Test
    void encodesText() {
        server.expect(requestTo(BASE_URL + "?text=a%20%26%20b"))
                .andRespond(withSuccess("false", TEXT_PLAIN));

        assertThat(profanityChecker.containsProfanity("a & b")).isFalse();
        server.verify();
    }

    @DisplayName("외부 API가 오류를 응답하면 원인을 담은 ProfanityCheckException이 발생한다")
    @Test
    void serverError() {
        server.expect(requestTo(BASE_URL + "?text=apple"))
                .andRespond(withStatus(INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> profanityChecker.containsProfanity("apple"))
                .isInstanceOf(ProfanityCheckException.class)
                .hasCauseInstanceOf(RestClientException.class);
    }

    @DisplayName("응답 본문이 비어 있으면 비속어가 없다고 단정하지 않고 예외가 발생한다")
    @Test
    void emptyResponse() {
        server.expect(requestTo(BASE_URL + "?text=apple"))
                .andRespond(withSuccess());

        assertThatThrownBy(() -> profanityChecker.containsProfanity("apple"))
                .isInstanceOf(ProfanityCheckException.class);
    }
}
