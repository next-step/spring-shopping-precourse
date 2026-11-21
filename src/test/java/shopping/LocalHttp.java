package shopping;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

final class LocalHttp {
    private final int port;

    LocalHttp(int port) {
        this.port = port;
    }

    HttpResponse<String> send(String method, String path, String body, String token)
            throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                .method(method, HttpRequest.BodyPublishers.ofString(body));
        if (!body.isEmpty()) {
            request.header("Content-Type", "application/json");
        }
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        try (var client = HttpClient.newHttpClient()) {
            return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    static JsonNode json(HttpResponse<String> response) {
        return JsonMapper.builder().build().readTree(response.body());
    }
}
