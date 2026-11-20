package shopping.product.profanity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * PurgoMalum(https://www.purgomalum.com) 비속어 검사 API 호출.
 * 외부 API 장애 시 상품 등록 자체가 막히지 않도록 검사를 통과시키고(fail-open) 경고 로그를 남긴다.
 */
@Slf4j
@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private static final String BASE_URL = "https://www.purgomalum.com";

    private final RestClient restClient = RestClient.builder()
            .baseUrl(BASE_URL)
            .build();

    @Override
    public boolean containsProfanity(String text) {
        try {
            String body = restClient.get()
                    .uri(uri -> uri.path("/service/containsprofanity")
                            .queryParam("text", text)
                            .build())
                    .retrieve()
                    .body(String.class);
            return "true".equalsIgnoreCase(body == null ? null : body.trim());
        } catch (Exception e) {
            log.warn("비속어 검사 API 호출에 실패하여 검사를 건너뜁니다. text={}", text, e);
            return false;
        }
    }
}
