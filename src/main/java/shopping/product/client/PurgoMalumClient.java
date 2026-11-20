package shopping.product.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class PurgoMalumClient {

    private final RestClient restClient;

    public PurgoMalumClient(
            @Value("${purgomalum.base-url}")
            String baseUrl,
            @Value("${purgomalum.connect-timeout}")
            Duration connectTimeout,
            @Value("${purgomalum.read-timeout}")
            Duration readTimeout) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public boolean containsProfanity(String text) {
        try {
            String body = restClient.get()
                    .uri("/service/containsprofanity?text={text}", text)
                    .retrieve()
                    .body(String.class);

            return "true".equals(body);
        } catch (RestClientException exception) {
            throw new BusinessException(ErrorCode.FAIL_CALL_PURGOMALUM);
        }
    }
}
