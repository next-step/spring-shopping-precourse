package shopping.product.infrastructure;

import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.ProfanityChecker;

@Component
public class PurgoMalumClient implements ProfanityChecker {

    private final HttpClient client;
    private final String endpoint;
    private final Duration timeout;

    public PurgoMalumClient(@Value("${shopping.profanity.url}") String endpoint,
            @Value("${shopping.profanity.timeout}") Duration timeout) {
        this.client = HttpClient.newBuilder().connectTimeout(timeout).build();
        this.endpoint = endpoint;
        this.timeout = timeout;
    }

    @Override
    public boolean containsProfanity(String text) {
        var response = client.sendAsync(request(text),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return interpret(await(response));
    }

    private HttpRequest request(String text) {
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8);
        return HttpRequest.newBuilder(URI.create(endpoint + "?text=" + encoded))
                .timeout(timeout).GET().build();
    }

    private HttpResponse<String> await(CompletableFuture<HttpResponse<String>> response) {
        try {
            return response.get(timeout.toNanos(), TimeUnit.NANOSECONDS);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw unavailable();
        } catch (ExecutionException | TimeoutException error) {
            throw unavailable();
        } finally {
            response.cancel(true);
        }
    }

    private boolean interpret(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw unavailable();
        }
        if ("true".equals(response.body().strip())) {
            return true;
        }
        if ("false".equals(response.body().strip())) {
            return false;
        }
        throw unavailable();
    }

    private ShoppingException unavailable() {
        return new ShoppingException(ErrorCode.PROFANITY_CHECK_UNAVAILABLE);
    }

    @PreDestroy
    public void close() {
        client.close();
    }
}
