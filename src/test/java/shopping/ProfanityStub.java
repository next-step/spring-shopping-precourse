package shopping;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

final class ProfanityStub implements AutoCloseable {
    private final HttpServer server;

    ProfanityStub() {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        server.createContext("/containsprofanity", ProfanityStub::respond);
        server.start();
    }

    String endpoint() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/containsprofanity";
    }

    private static void respond(HttpExchange exchange) throws IOException {
        try (exchange) {
            String query = URLDecoder.decode(exchange.getRequestURI().getRawQuery(), StandardCharsets.UTF_8);
            if (query.equals("text=검사 & 장애")) {
                exchange.sendResponseHeaders(502, -1);
                return;
            }
            String result = Boolean.toString(query.equals("text=금칙어 & 상품"));
            byte[] body = result.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
