package shopping.infrastructure;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import shopping.domain.ProfanityCheckException;
import shopping.domain.ProfanityCheckException.Reason;
import shopping.domain.ProfanityChecker;

public class PurgoMalumProfanityChecker implements ProfanityChecker {
    private static final String PATH = "/service/containsprofanity?text={text}";

    private final RestClient restClient;

    public PurgoMalumProfanityChecker(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean containsProfanity(String text) {
        String body = requestBody(text);
        return parse(body);
    }

    private boolean parse(String body) {
        if ("true".equals(body)) {
            return true;
        }
        if ("false".equals(body)) {
            return false;
        }
        throw new ProfanityCheckException(Reason.INVALID_RESPONSE,
                "PurgoMalum 응답 형식이 올바르지 않습니다: " + body);
    }

    private String requestBody(String text) {
        try {
            return restClient.get()
                    .uri(PATH, text)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, this::throwClientError)
                    .onStatus(HttpStatusCode::is5xxServerError, this::throwServerError)
                    .body(String.class);
        } catch (ResourceAccessException e) {
            throw new ProfanityCheckException(Reason.CONNECTION_ERROR,
                    "PurgoMalum에 연결할 수 없습니다: " + e.getMessage(), e);
        }
    }

    private void throwClientError(HttpRequest request, ClientHttpResponse response)
            throws IOException {
        throw new ProfanityCheckException(Reason.BAD_REQUEST,
                "PurgoMalum 요청 오류: " + response.getStatusCode());
    }

    private void throwServerError(HttpRequest request, ClientHttpResponse response)
            throws IOException {
        throw new ProfanityCheckException(Reason.SERVICE_ERROR,
                "PurgoMalum 서비스 오류: " + response.getStatusCode());
    }
}
