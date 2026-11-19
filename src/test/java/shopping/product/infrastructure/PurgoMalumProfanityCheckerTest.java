package shopping.product.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import shopping.common.ExternalApiException;

class PurgoMalumProfanityCheckerTest {

    private static final String BASE_URL = "https://purgomalum.test";

    private MockRestServiceServer server;
    private PurgoMalumProfanityChecker checker;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        checker = new PurgoMalumProfanityChecker(builder);
    }

    @Test
    void 응답이_true면_비속어가_포함된_것으로_판단한다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=badword"))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("badword")).isTrue();
    }

    @Test
    void 응답이_false면_비속어가_없는_것으로_판단한다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=apple"))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("apple")).isFalse();
    }

    @Test
    void 특수문자가_포함된_이름은_인코딩해서_전송한다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=A%2BB%20%26%20C"))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("A+B & C")).isFalse();
    }

    @Test
    void 외부_API가_실패하면_ExternalApiException이_발생한다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=apple"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> checker.containsProfanity("apple"))
                .isInstanceOf(ExternalApiException.class);
    }
}
