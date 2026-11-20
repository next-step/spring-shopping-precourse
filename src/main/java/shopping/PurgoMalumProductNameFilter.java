package shopping;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

final class PurgoMalumProductNameFilter implements ProductNameFilter {
    private final HttpClient client;
    private final URI endpoint;

    PurgoMalumProductNameFilter(HttpClient client, URI endpoint) {
        this.client = client;
        this.endpoint = endpoint;
    }

    @Override
    public boolean profanity(String productName) {
        var response = response(productName);
        if (response.statusCode() != 200) {
            throw new ProfanityCheckUnavailableException();
        }
        String result = response.body().strip();
        if ("true".equals(result)) {
            return true;
        }
        if ("false".equals(result)) {
            return false;
        }
        throw new ProfanityCheckUnavailableException();
    }

    private HttpResponse<String> response(String productName) {
        String text = URLEncoder.encode(productName, StandardCharsets.UTF_8);
        var request = HttpRequest.newBuilder(URI.create(endpoint + "?text=" + text))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ProfanityCheckUnavailableException(exception);
        } catch (IOException exception) {
            throw new ProfanityCheckUnavailableException(exception);
        }
    }
}
