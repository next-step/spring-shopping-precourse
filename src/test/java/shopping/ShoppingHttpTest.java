package shopping;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ShoppingHttpTest {
    private static final String PRODUCTS = "/api/products";
    private static final String REGISTER = "/api/members/register";
    private static final String LOGIN = "/api/members/login";
    private static final String WISHES = "/api/wishes";
    private static final String PASSWORD = "a-test-password";
    private static final String IMAGE = "https://example.com/product";
    private static final HttpServer PROFANITY = startProfanityServer();

    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper json = new JsonMapper();

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcClient jdbc;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> "jdbc:sqlite:file:shopping-http-test?mode=memory&cache=shared");
        registry.add("purgomalum.base-url",
                () -> "http://127.0.0.1:" + PROFANITY.getAddress().getPort());
    }

    @AfterAll
    static void stopProfanityServer() {
        PROFANITY.stop(0);
    }

    @Test
    void productCrudReturnsUpdatedDataAndRemovesTheProduct() throws Exception {
        long id = createProduct("Bag");
        String path = PRODUCTS + "/" + id;
        assertThat(json(request("GET", path, null, null), 200).get("name").asString()).isEqualTo("Bag");
        HttpResponse<String> changed = request("PUT", path, null, product("New Bag"));
        assertThat(json(changed, 200).get("name").asString()).isEqualTo("New Bag");
        JsonNode listing = json(request("GET", PRODUCTS, null, null), 200);
        assertThat(listing.values()).anySatisfy(item -> {
            assertThat(item.get("id").asLong()).isEqualTo(id);
            assertThat(item.get("name").asString()).isEqualTo("New Bag");
        });
        assertThat(request("DELETE", path, null, null).statusCode()).isEqualTo(204);
        assertError(request("GET", path, null, null), 404);
    }

    @ParameterizedTest
    @CsvSource({
        "POST, Bag, 0, 400, 가격",
        "POST, Forbidden, 1200, 400, 비속어",
        "PUT, Forbidden, 1200, 400, 비속어",
        "POST, Unavailable, 1200, 503, 검사"
    })
    void productWriteReturnsJsonErrors(String method, String name, int price, int status,
            String message) throws Exception {
        String path = PRODUCTS;
        if ("PUT".equals(method)) {
            path += "/" + createProduct("Update Target");
        }
        Map<String, Object> body = Map.of("name", name, "price", price, "imageUrl", IMAGE);
        assertThat(assertError(request(method, path, null, body), status)).contains(message);
    }

    @Test
    void registrationStoresOnlyHashesAndUniqueEmail() throws Exception {
        String token = register("Registration@Example.com");
        assertThat(memberPasswordHash("registration@example.com")).startsWith("$2").isNotEqualTo(PASSWORD);
        assertThat(jdbc.sql("SELECT token_hash FROM access_tokens WHERE member_id = "
                + "(SELECT id FROM members WHERE email = 'registration@example.com')")
                .query(String.class).single()).isNotEqualTo(token);
        assertError(request("POST", REGISTER, null, credentials(" registration@example.com ", PASSWORD)), 409);
        assertError(request("POST", REGISTER, null, credentials("invalid-email", PASSWORD)), 400);
    }

    @Test
    void loginIssuesAUsableTokenAndRejectsBothInvalidCredentialCases() throws Exception {
        String signupToken = register("login@example.com");
        String loginToken = json(request("POST", LOGIN, null, credentials("login@example.com", PASSWORD)), 200)
                .get("token").asString();
        assertThat(loginToken).isNotEqualTo(signupToken);
        assertThat(request("GET", WISHES, loginToken, null).statusCode()).isEqualTo(200);
        String wrong = assertError(request("POST", LOGIN, null, credentials("login@example.com", "wrong")), 401);
        String absent = assertError(request("POST", LOGIN, null, credentials("absent@example.com", PASSWORD)), 401);
        assertThat(wrong).isEqualTo(absent);
    }

    @Test
    void wishReadsAndDeletesAreScopedToTheAuthenticatedMember() throws Exception {
        String owner = register("owner@example.com");
        String other = register("other@example.com");
        long productId = createProduct("Book");
        long wishId = addWish(owner, productId);
        long otherWishId = addWish(other, productId);
        String path = WISHES + "/" + wishId;
        assertError(request("DELETE", path, other, null), 404);
        assertSingleWish(owner, wishId, productId);
        assertSingleWish(other, otherWishId, productId);
        assertThat(request("DELETE", path, owner, null).statusCode()).isEqualTo(204);
        assertThat(json(request("GET", WISHES, owner, null), 200).values()).isEmpty();
    }

    @Test
    void wishAdditionRejectsDuplicatesAndMissingProducts() throws Exception {
        String token = register("duplicate-wish@example.com");
        long productId = createProduct("Pen");
        addWish(token, productId);
        assertError(request("POST", WISHES, token, Map.of("productId", productId)), 409);
        assertError(request("POST", WISHES, token, Map.of("productId", Long.MAX_VALUE)), 404);
    }

    @Test
    void protectedRoutesRejectMissingAndExpiredTokens() throws Exception {
        String token = register("expiry@example.com");
        jdbc.sql("UPDATE access_tokens SET expires_at = 0 WHERE member_id = "
                + "(SELECT id FROM members WHERE email = 'expiry@example.com')").update();
        HttpResponse<String> missing = request("GET", WISHES, null, null);
        assertError(missing, 401);
        assertThat(missing.headers().firstValue("WWW-Authenticate")).contains("Bearer");
        assertError(request("POST", WISHES, token, Map.of("productId", 1)), 401);
        assertError(request("DELETE", WISHES + "/1", null, null), 401);
    }

    @Test
    void deletingProductAlsoDeletesItsWishRows() throws Exception {
        String token = register("cascade@example.com");
        long id = createProduct("Cascade");
        long wishId = addWish(token, id);
        assertThat(request("DELETE", PRODUCTS + "/" + id, null, null).statusCode()).isEqualTo(204);
        assertThat(jdbc.sql("SELECT COUNT(*) FROM wishes WHERE id = :id").param("id", wishId)
                .query(Long.class).single()).isZero();
    }

    private void assertSingleWish(String token, long wishId, long productId) throws Exception {
        JsonNode list = json(request("GET", WISHES, token, null), 200);
        assertThat(list.values()).singleElement().satisfies(wish -> {
            assertThat(wish.get("wishId").asLong()).isEqualTo(wishId);
            assertThat(wish.get("productId").asLong()).isEqualTo(productId);
            assertThat(wish.get("name").asString()).isEqualTo("Book");
        });
    }

    private String memberPasswordHash(String email) {
        return jdbc.sql("SELECT password_hash FROM members WHERE email = :email").param("email", email)
                .query(String.class).single();
    }

    private long addWish(String token, long productId) throws Exception {
        return json(request("POST", WISHES, token, Map.of("productId", productId)), 201)
                .get("wishId").asLong();
    }

    private long createProduct(String name) throws Exception {
        return json(request("POST", PRODUCTS, null, product(name)), 201).get("id").asLong();
    }

    private String register(String email) throws Exception {
        return json(request("POST", REGISTER, null, credentials(email, PASSWORD)), 201)
                .get("token").asString();
    }

    private static Map<String, Object> product(String name) {
        return Map.of("name", name, "price", 1200, "imageUrl", IMAGE);
    }

    private static Map<String, Object> credentials(String email, String password) {
        return Map.of("email", email, "password", password);
    }

    private String assertError(HttpResponse<String> response, int status) {
        String message = json(response, status).get("message").asString();
        assertThat(message).isNotBlank();
        return message;
    }

    private JsonNode json(HttpResponse<String> response, int status) {
        assertThat(response.statusCode()).as(response.body()).isEqualTo(status);
        return json.readTree(response.body());
    }

    private HttpResponse<String> request(String method, String path, String token, Map<String, ?> body)
            throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path));
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
        if (body != null) {
            request.header("Content-Type", "application/json");
            publisher = HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body));
        }
        return http.send(request.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());
    }

    private static HttpServer startProfanityServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/service/containsprofanity", ShoppingHttpTest::respondToProfanityCheck);
            server.start();
            return server;
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private static void respondToProfanityCheck(HttpExchange exchange) throws IOException {
        byte[] body = profanityResult(exchange.getRequestURI().getQuery()).getBytes(StandardCharsets.UTF_8);
        try (exchange) {
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
        }
    }

    private static String profanityResult(String query) {
        if ("text=Forbidden".equals(query)) {
            return "true";
        }
        if ("text=Unavailable".equals(query)) {
            return "unexpected";
        }
        return "false";
    }
}
