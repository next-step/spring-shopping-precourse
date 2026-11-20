package shopping.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

public final class ApiTestSupport {

    private ApiTestSupport() {}

    public static MvcTestResult postJson(MockMvcTester mvc, String uri, String body) {
        return mvc.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }

    public static void assertError(MvcTestResult result, HttpStatus status, String code) {
        assertThat(result).hasStatus(status);
        assertThat(result).bodyJson().extractingPath("$.code").isEqualTo(code);
    }

    public static void assertInvalidInput(MvcTestResult result, String... codes) {
        assertError(result, HttpStatus.BAD_REQUEST, "INVALID_INPUT");
        assertThat(result).bodyJson().extractingPath("$.errors[*].code").asArray()
                .containsExactlyInAnyOrder((Object[]) codes);
    }
}
