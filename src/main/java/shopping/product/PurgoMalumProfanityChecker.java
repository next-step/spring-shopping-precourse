package shopping.product;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private static final String BASE_URL = "https://www.purgomalum.com";

    private final RestClient restClient = RestClient.create(BASE_URL);

    @Override
    public boolean containsProfanity(String text) {
        String result = restClient.get()
                .uri("/service/containsprofanity?text={text}", text)
                .retrieve()
                .body(String.class);
        return Boolean.parseBoolean(result);
    }
}
