package shopping.product.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import shopping.product.application.ProfanityChecker;

@Component
public class PurgoMalumClient implements ProfanityChecker {

    private final RestClient restClient;

    public PurgoMalumClient(
            RestClient.Builder builder,
            @Value("${purgomalum.base-url:https://www.purgomalum.com}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public boolean containsProfanity(String text) {
        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/service/containsprofanity")
                        .queryParam("text", text)
                        .build())
                .retrieve()
                .body(String.class);
        return Boolean.parseBoolean(response);
    }
}
