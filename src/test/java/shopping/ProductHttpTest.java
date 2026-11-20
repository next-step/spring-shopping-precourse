package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class ProductHttpTest {
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
}
