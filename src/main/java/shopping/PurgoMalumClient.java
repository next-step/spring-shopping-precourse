package shopping;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PurgoMalumClient implements ProfanityChecker {

    private final RestClient restClient;

    @Autowired
    public PurgoMalumClient(RestClient.Builder builder) {
        HttpClient httpClient =
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient =
                builder.baseUrl("https://www.purgomalum.com").requestFactory(factory).build();
    }

    PurgoMalumClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public boolean containsProfanity(String text) {
        try {
            String response =
                    restClient
                            .get()
                            .uri("/service/containsprofanity?text={text}", text)
                            .retrieve()
                            .body(String.class);
            return parseResponse(response);
        } catch (RestClientException exception) {
            throw new ProfanityServiceUnavailableException(exception);
        }
    }

    private boolean parseResponse(String response) {
        if ("true".equals(response)) {
            return true;
        }
        if ("false".equals(response)) {
            return false;
        }
        throw new ProfanityServiceUnavailableException();
    }
}
