package shopping.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProfanityValidator {
    private final RestClient client;

    public ProfanityValidator(
            @Value("${shopping.profanity.base-url:https://www.purgomalum.com}") String baseUrl) {
        this.client = RestClient.create(baseUrl);
    }

    public void validate(String name) {
        String result = client.get()
                .uri(builder -> builder.path("/service/containsprofanity")
                        .queryParam("text", "{text}").build(name))
                .retrieve()
                .body(String.class);
        if (!"true".equals(result) && !"false".equals(result)) {
            throw new IllegalStateException("비속어 검사 응답을 해석할 수 없습니다");
        }
        if ("true".equals(result)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비속어는 사용할 수 없습니다");
        }
    }
}
