package shopping;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductApiTest {

    private static final String PRODUCT_REQUEST = """
            {"name": "아이폰 듀오", "price": 1000, "imageUrl": "https://example.com/a.png"}
            """;

    @Autowired
    private MockMvcTester mvc;

    @Test
    void 상품을_등록하면_생성된_id를_반환한다() {
        MvcTestResult result = mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCT_REQUEST)
                .exchange();

        assertThat(result)
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id")
                .isInstanceOf(Number.class);
    }

    @Test
    void 상품을_여러_번_등록하면_서로_다른_id가_반환된다() {
        MvcTestResult first = mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCT_REQUEST)
                .exchange();
        MvcTestResult second = mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCT_REQUEST)
                .exchange();

        assertThat(first).hasStatus(HttpStatus.CREATED);
        assertThat(second).hasStatus(HttpStatus.CREATED);
        String firstBody = new String(first.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        String secondBody = new String(second.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        assertThat(JsonPath.<Object>read(firstBody, "$.id")).isNotEqualTo(JsonPath.<Object>read(secondBody, "$.id"));
    }

    @Test
    void 상품_하나를_조회한다() {
        MvcTestResult created = mvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(PRODUCT_REQUEST)
                .exchange();
        String createdBody = new String(created.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        Number id = JsonPath.read(createdBody, "$.id");

        MvcTestResult result = mvc.get().uri("/api/products/{id}", id).exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.id").isEqualTo(id.intValue());
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("아이폰 듀오");
        assertThat(result).bodyJson().extractingPath("$.price").isEqualTo(1000);
        assertThat(result).bodyJson().extractingPath("$.imageUrl").isEqualTo("https://example.com/a.png");
    }
}
