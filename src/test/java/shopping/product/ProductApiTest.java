package shopping.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@Import(FakeProfanityChecker.class)
class ProductApiTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 이름, 가격, 이미지 URL을 입력받아 상품을 등록한다")
    void createProduct() {
        String request = """
                {"name": "사과", "price": 1000, "imageUrl": "https://example.com/apple.png"}
                """;

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.name").isEqualTo("사과");
    }

    @Test
    @DisplayName("상품 등록에 성공하면 상품 정보를 저장한다")
    void saveProduct() {
        String request = """
                {"name": "바나나", "price": 2000, "imageUrl": "https://example.com/banana.png"}
                """;

        assertThat(postProduct(request)).hasStatus(HttpStatus.CREATED);
        assertThat(productRepository.findAll())
                .extracting(Product::getName)
                .contains("바나나");
    }

    @Test
    @DisplayName("비속어가 포함된 상품 이름은 등록을 거부한다")
    void rejectProfanityName() {
        String request = """
                {"name": "badword", "price": 1000, "imageUrl": "https://example.com/bad.png"}
                """;

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("상품 이름에 비속어를 사용할 수 없습니다.");
        assertThat(productRepository.findAll())
                .extracting(Product::getName)
                .doesNotContain(FakeProfanityChecker.PROFANITY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "가나다라마바사아자차카타파하가나", "사과!"})
    @DisplayName("입력값이 유효하지 않으면 400과 오류 메시지를 응답한다")
    void respondBadRequestForInvalidName(String name) {
        String request = """
                {"name": "%s", "price": 1000, "imageUrl": "https://example.com/a.png"}
                """.formatted(name);

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").asString().isNotBlank();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("상품 가격이 0원 이하이면 400과 오류 메시지를 응답한다")
    void respondBadRequestForInvalidPrice(int price) {
        String request = """
                {"name": "사과", "price": %d, "imageUrl": "https://example.com/a.png"}
                """.formatted(price);

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("상품 가격은 1원 이상이어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("상품 이미지 URL이 비어 있으면 400과 오류 메시지를 응답한다")
    void respondBadRequestForBlankImageUrl(String imageUrl) {
        String request = """
                {"name": "사과", "price": 1000, "imageUrl": "%s"}
                """.formatted(imageUrl);

        assertThat(postProduct(request))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("상품 이미지 URL은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("상품 ID로 특정 상품의 정보를 조회한다")
    void findProductById() {
        Product saved = productRepository.save(new Product(
                new ProductName("포도"), new Price(3000), new ImageUrl("https://example.com/grape.png")));

        assertThat(mockMvc.get().uri("/api/products/{productId}", saved.getId()))
                .hasStatusOk()
                .bodyJson()
                .isLenientlyEqualTo("""
                        {"id": %d, "name": "포도", "price": 3000,
                         "imageUrl": "https://example.com/grape.png"}
                        """.formatted(saved.getId()));
    }

    @Test
    @DisplayName("등록된 모든 상품의 목록을 조회한다")
    void findAllProducts() {
        saveProduct("수박");
        saveProduct("참외");

        assertThat(mockMvc.get().uri("/api/products"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[*].name").asArray()
                .containsExactlyInAnyOrder("수박", "참외");
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회하면 404와 오류 메시지를 응답한다")
    void respondNotFoundForUnknownProduct() {
        assertThat(mockMvc.get().uri("/api/products/{productId}", Long.MAX_VALUE))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("상품 ID로 기존 상품의 이름, 가격, 이미지 URL을 수정한다")
    void updateProduct() {
        Product saved = saveProduct("사과");
        String request = """
                {"name": "청사과", "price": 2000, "imageUrl": "https://example.com/green.png"}
                """;

        assertThat(putProduct(saved.getId(), request))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.name").isEqualTo("청사과");
        Product updated = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("청사과");
        assertThat(updated.getPrice()).isEqualTo(2000);
        assertThat(updated.getImageUrl()).isEqualTo("https://example.com/green.png");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "가나다라마바사아자차카타파하가나", "사과!", "badword"})
    @DisplayName("상품 수정 시에도 상품 이름에 대한 유효성 검사를 수행한다")
    void validateNameOnUpdate(String name) {
        Product saved = saveProduct("사과");
        String request = """
                {"name": "%s", "price": 1000, "imageUrl": "https://example.com/a.png"}
                """.formatted(name);

        assertThat(putProduct(saved.getId(), request)).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(productRepository.findById(saved.getId()).orElseThrow().getName())
                .isEqualTo("사과");
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 수정하면 404와 오류 메시지를 응답한다")
    void respondNotFoundOnUpdate() {
        String request = """
                {"name": "사과", "price": 1000, "imageUrl": "https://example.com/a.png"}
                """;

        assertThat(putProduct(Long.MAX_VALUE, request))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("상품 ID로 특정 상품을 삭제한다")
    void deleteProduct() {
        Product saved = saveProduct("사과");

        assertThat(mockMvc.delete().uri("/api/products/{productId}", saved.getId()))
                .hasStatus(HttpStatus.NO_CONTENT);
        assertThat(productRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 삭제하면 404와 오류 메시지를 응답한다")
    void respondNotFoundOnDelete() {
        assertThat(mockMvc.delete().uri("/api/products/{productId}", Long.MAX_VALUE))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("존재하지 않는 상품입니다.");
    }

    private Product saveProduct(String name) {
        return productRepository.save(new Product(
                new ProductName(name), new Price(1000), new ImageUrl("https://example.com/item.png")));
    }

    private MockMvcTester.MockMvcRequestBuilder postProduct(String body) {
        return mockMvc.post()
                .uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    private MockMvcTester.MockMvcRequestBuilder putProduct(Long id, String body) {
        return mockMvc.put()
                .uri("/api/products/{productId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }
}
