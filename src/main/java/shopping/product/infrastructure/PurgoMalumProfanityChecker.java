package shopping.product.infrastructure;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import shopping.common.ExternalApiException;
import shopping.product.domain.ProfanityChecker;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final RestClient restClient;

    @Autowired
    public PurgoMalumProfanityChecker(@Value("${purgomalum.base-url}") String baseUrl) {
        this(RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory()));
    }

    PurgoMalumProfanityChecker(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public boolean containsProfanity(String text) {
        try {
            String result = restClient.get()
                    .uri("/service/containsprofanity?text={text}", text)
                    .retrieve()
                    .body(String.class);
            return Boolean.parseBoolean(result);
        } catch (RestClientException exception) {
            throw new ExternalApiException("비속어 검사 서비스 호출에 실패했습니다.", exception);
        }
    }

    private static SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT);
        factory.setReadTimeout(TIMEOUT);
        return factory;
    }
}
