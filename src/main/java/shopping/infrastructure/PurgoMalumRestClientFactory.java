package shopping.infrastructure;

import java.time.Duration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public final class PurgoMalumRestClientFactory {
    public static final String BASE_URL = "https://www.purgomalum.com";
    public static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    public static final Duration READ_TIMEOUT = Duration.ofSeconds(3);

    private PurgoMalumRestClientFactory() {
    }

    public static RestClient create(String baseUrl, Duration connectTimeout,
            Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
