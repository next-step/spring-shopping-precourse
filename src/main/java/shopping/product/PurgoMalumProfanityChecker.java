package shopping.product;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import shopping.common.ExternalServiceException;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private static final String URL =
            "https://www.purgomalum.com/service/containsprofanity?text={text}";

    private final RestClient restClient = RestClient.create();

    @Override
    public boolean containsProfanity(String text) {
        try {
            String result = restClient.get()
                    .uri(URL, text)
                    .retrieve()
                    .body(String.class);
            return Boolean.parseBoolean(result);
        } catch (RestClientException e) {
            throw new ExternalServiceException("비속어 검사 서비스를 이용할 수 없습니다.", e);
        }
    }
}
