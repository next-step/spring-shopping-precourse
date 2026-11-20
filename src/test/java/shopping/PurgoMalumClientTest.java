package shopping;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PurgoMalumClientTest {
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final PurgoMalumClient client = new PurgoMalumClient(builder);

    @Test
    void 검사_API가_false를_반환하면_상품명을_허용한다() {
        expectRequest().andRespond(withSuccess("false", MediaType.TEXT_PLAIN));

        assertThatCode(() -> client.validate("mug")).doesNotThrowAnyException();
        server.verify();
    }

    @Test
    void 검사_API가_true를_반환하면_상품명을_거절한다() {
        expectRequest().andRespond(withSuccess("true", MediaType.TEXT_PLAIN));

        assertError(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 검사_API에서_오류가_발생하면_503을_반환한다() {
        expectRequest().andRespond(withServerError());

        assertError(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "unexpected"})
    void 검사_API의_응답을_해석할_수_없으면_503을_반환한다(String body) {
        expectRequest().andRespond(withSuccess(body, MediaType.TEXT_PLAIN));

        assertError(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private ResponseActions expectRequest() {
        return server.expect(requestTo("https://www.purgomalum.com/service/containsprofanity?text=mug"))
                .andExpect(method(HttpMethod.GET));
    }

    private void assertError(HttpStatus expected) {
        assertThatExceptionOfType(ResponseStatusException.class)
                .isThrownBy(() -> client.validate("mug"))
                .satisfies(error -> assertThat(error.getStatusCode()).isEqualTo(expected));
        server.verify();
    }
}
