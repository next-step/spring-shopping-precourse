package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PurgoMalumProfanityCheckerTest {

    private static final String BASE_URL = "https://www.purgomalum.com";

    private MockRestServiceServer server;
    private PurgoMalumProfanityChecker checker;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        checker = new PurgoMalumProfanityChecker(builder.build());
    }

    @Test
    void 응답이_true이면_비속어가_있다고_판단한다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=badword"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("badword")).isTrue();
        server.verify();
    }

    @Test
    void 응답이_false이면_비속어가_없다고_판단한다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=coffee"))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("coffee")).isFalse();
        server.verify();
    }

    @Test
    void 공백과_특수_문자는_인코딩해서_보낸다() {
        server.expect(requestTo(BASE_URL + "/service/containsprofanity?text=A%26B%20C"))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThat(checker.containsProfanity("A&B C")).isFalse();
        server.verify();
    }
}
