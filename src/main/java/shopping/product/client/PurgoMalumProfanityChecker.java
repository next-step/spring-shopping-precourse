package shopping.product.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import shopping.product.domain.ProfanityChecker;
import shopping.product.exception.ProfanityCheckUnavailableException;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {
    private final RestClient restClient;

    public PurgoMalumProfanityChecker(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean containsProfanity(String text) {
        return parseResult(requestResult(text));
    }

    private String requestResult(String text) {
        try {
            return restClient.get()
                    .uri("/service/containsprofanity?text={text}", text)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new ProfanityCheckUnavailableException(
                    "비속어 검사 서비스를 사용할 수 없습니다.", e);
        }
    }

    private boolean parseResult(String body) {
        if ("true".equals(body)) {
            return true;
        }
        if ("false".equals(body)) {
            return false;
        }
        throw new ProfanityCheckUnavailableException("비속어 검사 서비스의 응답을 해석할 수 없습니다.");
    }
}