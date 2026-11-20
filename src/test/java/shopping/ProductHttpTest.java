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
class ProductHttpTest {
    private static final ProfanityStub profanity = new ProfanityStub();

    @DynamicPropertySource
    static void profanityEndpoint(DynamicPropertyRegistry properties) {
        properties.add("shopping.profanity.endpoint", profanity::endpoint);
    }

    @AfterAll
    static void stopProfanityStub() {
        profanity.close();
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository products;

    @Test
    void 목록은_등록_순서대로_조회한다() throws Exception {
        var first = products.registeredProduct("치이카와", 1000L, "https://example.com/first");
        var second = products.registeredProduct("하치와레", 2000L, "https://example.com/second");

        var response = new LocalHttp(port).send("GET", "/api/products", "", null);
        var body = LocalHttp.json(response);

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(body.get(body.size() - 2).get("id").asLong()).isEqualTo(first.id());
        Assertions.assertThat(body.get(body.size() - 1).get("id").asLong()).isEqualTo(second.id());
    }

    @Test
    void ID로_상품을_조회한다() throws Exception {
        var product = products.registeredProduct("우사기", 3000L, "https://example.com/usagi");

        var response = new LocalHttp(port).send("GET", "/api/products/" + product.id(), "", null);
        var body = LocalHttp.json(response);

        Assertions.assertThat(response.statusCode()).isEqualTo(200);
        Assertions.assertThat(body.get("id").asLong()).isEqualTo(product.id());
        Assertions.assertThat(body.get("name").asString()).isEqualTo(product.name());
        Assertions.assertThat(body.get("price").asLong()).isEqualTo(product.price());
        Assertions.assertThat(body.get("url").asString()).isEqualTo(product.url());
    }

    @Test
    void 없는_상품은_404다() throws Exception {
        var response = new LocalHttp(port).send("GET", "/api/products/999999", "", null);

        Assertions.assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    void 무료_상품을_등록하고_Location으로_조회한다() throws Exception {
        var http = new LocalHttp(port);
        var created = http.send("POST", "/api/products", """
                {"name":"무료 상품","price":0,"url":"https://example.com/free"}
                """, null);

        Assertions.assertThat(created.statusCode()).isEqualTo(201);
        String location = created.headers().firstValue("Location").orElseThrow();
        var found = http.send("GET", location, "", null);
        Assertions.assertThat(found.statusCode()).isEqualTo(200);
        Assertions.assertThat(LocalHttp.json(found)).isEqualTo(LocalHttp.json(created));
        Assertions.assertThat(LocalHttp.json(created).get("price").asLong()).isZero();
    }

    @Test
    void 가격을_생략하면_등록할_수_없다() throws Exception {
        var response = new LocalHttp(port).send("POST", "/api/products", """
                {"name":"치이카와","url":"https://example.com/first"}
                """, null);

        Assertions.assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    void 상품을_수정하면_같은_ID로_새_정보를_조회한다() throws Exception {
        var product = products.registeredProduct("우사기", 3000L, "https://example.com/usagi");
        var http = new LocalHttp(port);
        String path = "/api/products/" + product.id();

        var updated = http.send("PUT", path, """
                {"name":"새 상품","price":500,"url":"https://example.com/new"}
                """, null);

        Assertions.assertThat(updated.statusCode()).isEqualTo(204);
        var body = LocalHttp.json(http.send("GET", path, "", null));
        Assertions.assertThat(body.get("name").asString()).isEqualTo("새 상품");
        Assertions.assertThat(body.get("price").asLong()).isEqualTo(500L);
    }

    @Test
    void 상품을_삭제하면_다시_조회할_수_없다() throws Exception {
        var product = products.registeredProduct("치이카와", 1000L, "https://example.com/first");
        var http = new LocalHttp(port);
        String path = "/api/products/" + product.id();

        Assertions.assertThat(http.send("DELETE", path, "", null).statusCode()).isEqualTo(204);
        Assertions.assertThat(http.send("GET", path, "", null).statusCode()).isEqualTo(404);
        Assertions.assertThat(http.send("DELETE", path, "", null).statusCode()).isEqualTo(404);
    }

    @Test
    void 비속어가_포함된_상품은_등록할_수_없다() throws Exception {
        var before = products.products();
        var response = new LocalHttp(port).send("POST", "/api/products", """
                {"name":"금칙어 & 상품","price":1000,"url":"https://example.com/item"}
                """, null);

        Assertions.assertThat(response.statusCode()).isEqualTo(400);
        Assertions.assertThat(products.products()).isEqualTo(before);
    }

    @Test
    void 이름_검사에_실패하면_기존_상품을_유지한다() throws Exception {
        var product = products.registeredProduct("치이카와", 1000L, "https://example.com/first");
        var response = new LocalHttp(port).send("PUT", "/api/products/" + product.id(), """
                {"name":"검사 & 장애","price":2000,"url":"https://example.com/new"}
                """, null);

        Assertions.assertThat(response.statusCode()).isEqualTo(503);
        Assertions.assertThat(products.product(product.id())).isEqualTo(product);
    }

    // 잘못된 가격으로 수정할 때 기존 상품을 유지하는지도 확인할 수 있다. 생략.
}
