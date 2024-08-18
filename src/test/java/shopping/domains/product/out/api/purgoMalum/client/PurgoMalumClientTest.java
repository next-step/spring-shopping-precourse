package shopping.domains.product.out.api.purgoMalum.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "spring.cloud.openfeign.client.config.purgoMalumClient.url=http://localhost:${wiremock.server.port}"
})
public class PurgoMalumClientTest {
    @Autowired
    private PurgoMalumClient purgoMalumClient;

    @BeforeEach
    void setup() {
        WireMock.stubFor(get(urlPathEqualTo("/containsprofanity"))
                .withQueryParam("text", equalTo("badword"))
                .willReturn(aResponse().withBody("true")));

        WireMock.stubFor(get(urlPathEqualTo("/containsprofanity"))
                .withQueryParam("text", equalTo("goodword"))
                .willReturn(aResponse().withBody("false")));
    }

    @ParameterizedTest
    @DisplayName("비속어 포함 여부 테스트")
    @MethodSource("containsProfanityParameters")
    void containsProfanityTest(
            @NonNull final String given,
            final boolean expected
    ) {
        final boolean actual = purgoMalumClient.containsProfanity(given);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> containsProfanityParameters() {
        return Stream.of(
                Arguments.of("badword", true),
                Arguments.of("goodword", false)
        );
    }
}