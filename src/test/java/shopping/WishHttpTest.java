package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class WishHttpTest {
    private static final ProfanityStub profanity = new ProfanityStub();
    private static final String WISHES = "/api/wishes";

    @LocalServerPort
    private int port;

    @Autowired
    private TokenRepository tokens;

    @Autowired
    private ProductRepository products;

    @DynamicPropertySource
    static void profanityEndpoint(DynamicPropertyRegistry properties) {
        properties.add("shopping.profanity.endpoint", profanity::endpoint);
    }

    @AfterAll
    static void stopProfanityStub() {
        profanity.close();
    }

    @Test
    void 같은_상품을_다시_담으면_내_목록의_수량만_늘어난다() throws Exception {
        var item = product();
        String token = tokens.token(1L);
        long wishId = wishId(item, token);

        Assertions.assertThat(wishId(item, token)).isEqualTo(wishId);
        var response = http().send("GET", WISHES, "", token);
        var list = LocalHttp.json(response);

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(list.size()).isEqualTo(1);
        Assertions.assertThat(list.get(0).get("quantity").asLong()).isEqualTo(2L);
        Assertions.assertThat(list.get(0).get("product").get("id").asLong()).isEqualTo(item.id());
        Assertions.assertThat(LocalHttp.json(http().send("GET", WISHES, "", tokens.token(2L))).size()).isZero();
    }

    @Test
    void 토큰이_없거나_유효하지_않으면_위시를_사용할_수_없다() throws Exception {
        Assertions.assertThat(http().send("GET", WISHES, "", null).statusCode()).isEqualTo(401);
        Assertions.assertThat(http().send("POST", WISHES, "{\"productId\":1}", "unknown").statusCode())
                .isEqualTo(401);
    }

    @Test
    void 삭제된_상품은_목록에서_숨기고_다시_담을_수_없다() throws Exception {
        var item = product();
        String token = tokens.token(3L);
        wishId(item, token);
        products.remove(item.id());

        Assertions.assertThat(LocalHttp.json(http().send("GET", WISHES, "", token)).size()).isZero();
        Assertions.assertThat(http().send("POST", WISHES, body(item), token).statusCode()).isEqualTo(404);
    }

    @Test
    void 수량이_2인_내_위시도_한번에_삭제한다() throws Exception {
        var item = product();
        String token = tokens.token(4L);
        wishId(item, token);
        String path = WISHES + "/" + wishId(item, token);

        Assertions.assertThat(http().send("DELETE", path, "", token).statusCode()).isEqualTo(204);
        Assertions.assertThat(LocalHttp.json(http().send("GET", WISHES, "", token)).size()).isZero();
    }

    @Test
    void 다른_회원의_위시나_없는_위시는_삭제할_수_없다() throws Exception {
        String owner = tokens.token(5L);
        String other = tokens.token(6L);
        long wishId = wishId(product(), owner);
        String path = WISHES + "/" + wishId;

        Assertions.assertThat(http().send("DELETE", path, "", other).statusCode()).isEqualTo(404);
        Assertions.assertThat(http().send("DELETE", WISHES + "/999999", "", owner).statusCode())
                .isEqualTo(404);
        var list = LocalHttp.json(http().send("GET", WISHES, "", owner));
        Assertions.assertThat(list.get(0).get("wishId").asLong()).isEqualTo(wishId);
    }

    private Product product() {
        return products.registeredProduct("치이카와", 1000L, "https://example.com/item");
    }

    private long wishId(Product item, String token) throws Exception {
        var response = http().send("POST", WISHES, body(item), token);
        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        var json = LocalHttp.json(response);
        Assertions.assertThat(json.get("productId").asLong()).isEqualTo(item.id());
        return json.get("wishId").asLong();
    }

    private String body(Product item) {
        return "{\"productId\":%d}".formatted(item.id());
    }

    private LocalHttp http() {
        return new LocalHttp(port);
    }
}
