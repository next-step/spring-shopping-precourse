package shopping.product;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;

@Component
public class ProfanityValidator {
    private final RestClient client;

    public ProfanityValidator(
            @Value("${shopping.profanity.base-url:https://www.purgomalum.com}") String baseUrl) {
        this(RestClient.create(baseUrl));
    }

    ProfanityValidator(RestClient client) {
        this.client = client;
    }

    public void validate(String name) {
        String result = client.get()
                .uri(builder -> buildUri(builder, name))
                .retrieve()
                .body(String.class);
        if (!"true".equals(result) && !"false".equals(result)) {
            throw new IllegalStateException("비속어 검사 응답을 해석할 수 없습니다");
        }
        if ("true".equals(result)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비속어는 사용할 수 없습니다");
        }
    }

    private URI buildUri(UriBuilder builder, String name) {
        URI uri = builder.path("/service/containsprofanity")
                .queryParam("text", name).build();
        return URI.create(uri.toASCIIString().replace("+", "%2B"));
    }
}
