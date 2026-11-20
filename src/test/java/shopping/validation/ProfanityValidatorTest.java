package shopping.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

class ProfanityValidatorTest {
    private MockRestServiceServer server;
    private ProfanityValidator profanityValidator;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://www.purgomalum.com");
        server = MockRestServiceServer.bindTo(builder).build();
        profanityValidator = new ProfanityValidator(builder.build());
    }

    @Test
    @DisplayName("비속어 검사 결과가 true이면 상품 이름을 거절한다")
    void rejectProfanity() {
        server.expect(queryParam("text", "sample"))
                .andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> profanityValidator.validate("sample"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("비속어");
        server.verify();
    }

    @Test
    @DisplayName("비속어 검사 결과가 false이면 상품 이름을 허용한다")
    void acceptCleanName() {
        server.expect(queryParam("text", "sample"))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThatCode(() -> profanityValidator.validate("sample"))
                .doesNotThrowAnyException();
        server.verify();
    }

    @Test
    @DisplayName("상품 이름을 원래 값대로 비속어 검사 API에 전달한다")
    void passOriginalName() {
        String name = "상품 A+B&/_";
        server.expect(request -> assertThat(decodeText(request.getURI().getRawQuery()))
                        .isEqualTo(name))
                .andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        profanityValidator.validate(name);

        server.verify();
    }

    private String decodeText(String query) {
        return URLDecoder.decode(query.substring("text=".length()), StandardCharsets.UTF_8);
    }
}
