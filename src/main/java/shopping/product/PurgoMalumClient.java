package shopping.product;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PurgoMalumClient {

    private final RestClient restClient = RestClient.create("https://www.purgomalum.com");

    public boolean containsProfanity(String text) {
        String result = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/service/containsprofanity")
                        .queryParam("text", text)
                        .build())
                .retrieve()
                .body(String.class);
        return Boolean.parseBoolean(result);
    }
}