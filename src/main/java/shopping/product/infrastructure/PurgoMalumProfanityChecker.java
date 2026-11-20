package shopping.product.infrastructure;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import shopping.product.application.ProfanityCheckUnavailableException;
import shopping.product.application.ProfanityChecker;
import shopping.product.domain.ProductName;

@Component
public final class PurgoMalumProfanityChecker implements ProfanityChecker {
    private final RestClient restClient;

    public PurgoMalumProfanityChecker(
            @Value("${purgomalum.base-url:https://www.purgomalum.com}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public boolean containsProfanity(ProductName name) {
        String body = requestProfanityResult(name);
        if ("true".equals(body)) {
            return true;
        }
        if ("false".equals(body)) {
            return false;
        }
        throw new ProfanityCheckUnavailableException("비속어 검사 서비스가 예상 밖 응답을 보냈습니다.");
    }

    private String requestProfanityResult(ProductName name) {
        try {
            return restClient.get()
                    .uri("/service/containsprofanity?text={name}", name.name())
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (request, response) -> {
                        throw new ProfanityCheckUnavailableException("비속어 검사 요청에 실패했습니다.");
                    })
                    .body(String.class);
        } catch (RestClientException exception) {
            throw new ProfanityCheckUnavailableException("비속어 검사 서비스에 연결하지 못했습니다.", exception);
        }
    }
}
