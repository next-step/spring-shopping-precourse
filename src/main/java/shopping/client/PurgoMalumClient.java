package shopping.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

@Component
public class PurgoMalumClient {

    private static final String PURGOMALUM_URL = "https://www.purgomalum.com/service/containsprofanity?text=";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public boolean containsProfanity(String text) {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PURGOMALUM_URL + encoded))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Boolean.parseBoolean(response.body().trim());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("비속어 검사 API 호출에 실패했습니다.", e);
        }
    }
}
