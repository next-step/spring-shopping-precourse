package shopping.product.util;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProfanityChecker {
    private static final String PURGOMALUM_BASE_URL = "https://www.purgomalum.com";
    private static final String CONTAINS_PROFANITY_PATH = "/service/containsprofanity";

    private final RestClient restClient = RestClient.create(PURGOMALUM_BASE_URL);

    public void validate(String name) {
        String result = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(CONTAINS_PROFANITY_PATH)
                        .queryParam("text", name)
                        .build())
                .retrieve()
                .body(String.class);
        if (Boolean.parseBoolean(result)) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함되어 있습니다.");
        }
    }
}
