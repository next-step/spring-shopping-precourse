package shopping.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PurgoMalumProfanityChecker implements ProfanityChecker {

    private static final String CHECK_FAILED_MESSAGE = "Failed to check profanity";

    private final RestClient restClient;

    public PurgoMalumProfanityChecker(RestClient.Builder builder,
                                      @Value("${purgomalum.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @Override
    public boolean containsProfanity(String text) {
        String body = requestContainsProfanity(text);

        if (body == null) {
            throw new ProfanityCheckException(CHECK_FAILED_MESSAGE + " : empty response");
        }

        return Boolean.parseBoolean(body.trim());
    }

    private String requestContainsProfanity(String text) {
        try {
            return restClient.get()
                    .uri("/service/containsprofanity?text={text}", text)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new ProfanityCheckException(CHECK_FAILED_MESSAGE, e);
        }
    }
}
