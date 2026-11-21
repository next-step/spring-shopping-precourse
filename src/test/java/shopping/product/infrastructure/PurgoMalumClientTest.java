package shopping.product.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class PurgoMalumClientTest {

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final PurgoMalumClient client =
            new PurgoMalumClient(builder, "https://www.purgomalum.com");

    @ParameterizedTest
    @CsvSource({"badword,true", "clean,false"})
    @DisplayName("PurgoMalum API를 호출해 비속어 포함 여부를 확인한다")
    void containsProfanity(String text, boolean expected) {
        server.expect(requestTo(
                        "https://www.purgomalum.com/service/containsprofanity?text=" + text))
                .andRespond(withSuccess(Boolean.toString(expected), MediaType.TEXT_PLAIN));

        assertThat(client.containsProfanity(text)).isEqualTo(expected);
        server.verify();
    }
}
