package shopping;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Component;

@Component

public class PurgoMalumClient {
    private final RestClient client;

    public PurgoMalumClient() {
        this(defaultBuilder());
    }

    PurgoMalumClient(RestClient.Builder builder) {
        this.client = builder.baseUrl("https://www.purgomalum.com").build();
    }

    private static RestClient.Builder defaultBuilder() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        return RestClient.builder().requestFactory(factory);
    }

    public void validate(String name) {
        String result = fetchResult(name);
        if ("true".equals(result)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품명에 비속어가 포함되어 있습니다.");
        }
        if (!"false".equals(result)) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "상품명을 검사할 수 없습니다.");
        }
    }

    private String fetchResult(String name) {
        try {
            return client.get()
                    .uri("/service/containsprofanity?text={name}", name)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "상품명을 검사할 수 없습니다.", e);
        }
    }
}

