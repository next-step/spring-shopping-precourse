package shopping.product.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.application.ProfanityChecker;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private ProfanityChecker profanityChecker;

    @Test
    @DisplayName("POST /api/products로 상품을 생성하면 201과 생성된 상품을 반환한다")
    void create() {
        String body = """
                {"name": "캐러멜 팝콘", "price": 5000,
                 "imageUrl": "https://example.com/popcorn.jpg"}
                """;

        MvcTestResult result = mockMvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("캐러멜 팝콘");
        assertThat(result).bodyJson().extractingPath("$.price").isEqualTo(5_000);
        assertThat(result).bodyJson().extractingPath("$.imageUrl")
                .isEqualTo("https://example.com/popcorn.jpg");
        assertThat(productRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("GET /api/products/{productId}로 특정 상품을 조회한다")
    void findById() {
        Product product = productRepository.save(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"));

        MvcTestResult result = mockMvc.get().uri("/api/products/{productId}", product.getId())
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").isEqualTo(product.getId().intValue());
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("캐러멜 팝콘");
        assertThat(result).bodyJson().extractingPath("$.price").isEqualTo(5_000);
        assertThat(result).bodyJson().extractingPath("$.imageUrl")
                .isEqualTo("https://example.com/popcorn.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 상품을 GET /api/products/{productId}로 조회하면 예외가 발생한다")
    void findByUnknownId() {
        MvcTestResult result = mockMvc.get().uri("/api/products/{productId}", 999L)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("GET /api/products로 모든 상품 목록을 조회한다")
    void findAll() throws Exception {
        productRepository.saveAll(List.of(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"),
                new Product("콜라", 2_000L, "https://example.com/cola.jpg")));

        MvcTestResult result = mockMvc.get().uri("/api/products").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result.getResponse().getContentAsString())
                .contains("캐러멜 팝콘", "콜라", "https://example.com/popcorn.jpg");
    }

    @Test
    @DisplayName("PUT /api/products/{productId}로 기존 상품의 정보를 수정한다")
    void update() {
        Product product = productRepository.save(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"));
        String body = """
                {"name": "콜라", "price": 2000,
                 "imageUrl": "https://example.com/cola.jpg"}
                """;

        MvcTestResult result = mockMvc.put().uri("/api/products/{productId}", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("콜라");
        assertThat(result).bodyJson().extractingPath("$.price").isEqualTo(2_000);
        assertThat(result).bodyJson().extractingPath("$.imageUrl")
                .isEqualTo("https://example.com/cola.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 상품을 PUT /api/products/{productId}로 수정하면 예외가 발생한다")
    void updateUnknownProduct() {
        String body = """
                {"name": "콜라", "price": 2000,
                 "imageUrl": "https://example.com/cola.jpg"}
                """;

        MvcTestResult result = mockMvc.put().uri("/api/products/{productId}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("DELETE /api/products/{productId}로 특정 상품을 삭제한다")
    void delete() {
        Product product = productRepository.save(
                new Product("캐러멜 팝콘", 5_000L, "https://example.com/popcorn.jpg"));

        MvcTestResult result = mockMvc.delete().uri("/api/products/{productId}", product.getId())
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(productRepository.existsById(product.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 상품을 DELETE /api/products/{productId}로 삭제하면 예외가 발생한다")
    void deleteUnknownProduct() {
        MvcTestResult result = mockMvc.delete().uri("/api/products/{productId}", 999L)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("비속어가 포함된 이름으로 상품을 생성하면 예외가 발생한다")
    void createWithProfanity() {
        String body = """
                {"name": "badword", "price": 5000,
                 "imageUrl": "https://example.com/product.jpg"}
                """;
        when(profanityChecker.containsProfanity("badword")).thenReturn(true);

        MvcTestResult result = mockMvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("유효하지 않은 상품 요청은 400과 JSON 오류 메시지를 반환한다")
    void createWithInvalidRequest() {
        String body = """
                {"name": "1234567890123456", "price": -1, "imageUrl": ""}
                """;

        MvcTestResult result = mockMvc.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }
}
