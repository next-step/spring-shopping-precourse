package shopping.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private static final String PATH = "/service/containsprofanity";

    private final RestClient restClient;

    @Autowired
    public PurgoMalumProfanityChecker(@Value("${purgomalum.base-url}") String baseUrl) {
        this(RestClient.builder().baseUrl(baseUrl).build());
    }

    PurgoMalumProfanityChecker(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean containsProfanity(String text) {
        String body = restClient.get()
                .uri(uriBuilder -> uriBuilder.path(PATH).queryParam("text", "{text}").build(text))
                .retrieve()
                .body(String.class);
        return Boolean.parseBoolean(body);
    }
}
