package shopping.product;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private final RestClient client = RestClient.create("https://www.purgomalum.com");

    @Override
    public boolean contains(String text) {
        String answer = client.get()
                .uri("/service/containsprofanity?text={text}", text)
                .retrieve()
                .body(String.class);
        return Boolean.parseBoolean(answer);
    }
}
