package shopping.product.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.Product;
import shopping.product.domain.ProfanityChecker;
import shopping.product.persistence.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("상품 컨트롤러")
class ProductControllerTest {

    private static final String CREATE_BODY = "{\"name\":\"상품\",\"price\":1000,"
            + "\"imageUrl\":\"https://example.com/image.png\"}";
    private static final String UPDATE_BODY =
            "{\"name\":\"변경\",\"price\":2000,\"imageUrl\":\"https://b.com\"}";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private ProductRepository products;
    @MockitoBean
    private ProfanityChecker checker;

    @BeforeEach
    @AfterEach
    void clear() {
        jdbc.update("delete from products");
    }

    @DisplayName("상품 추가에 성공하면 저장된 상품 정보와 생성 상태를 반환한다")
    @Test
    void storesProductAndReturnsCreatedDetails() throws Exception {
        create(CREATE_BODY).andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("상품"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.png"));

        assertThat(jdbc.queryForObject("select count(*) from products", Integer.class))
                .isEqualTo(1);
    }

    @DisplayName("잘못된 가격으로 상품 추가를 요청하면 저장하지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1.5", "2147483648", "\"1000\"", "null"})
    void invalidPriceDoesNotStoreProduct(String price) throws Exception {
        create(CREATE_BODY.replace("1000", price)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        assertEmpty();
    }

    @DisplayName("비속어가 있는 상품 추가 요청은 저장 없이 입력 오류로 응답한다")
    @Test
    void profanityIsInputFailureWithoutStorage() throws Exception {
        when(checker.containsProfanity(anyString())).thenReturn(true);

        create(CREATE_BODY).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PROFANITY_DETECTED"));

        assertEmpty();
    }

    @DisplayName("비속어 검사 장애 시 저장 없이 서비스 이용 불가로 응답한다")
    @Test
    void checkerFailureReturnsServiceUnavailableWithoutStorage() throws Exception {
        when(checker.containsProfanity(anyString()))
                .thenThrow(new ShoppingException(ErrorCode.PROFANITY_CHECK_UNAVAILABLE));

        create(CREATE_BODY).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("PROFANITY_CHECK_UNAVAILABLE"));

        assertEmpty();
    }

    @DisplayName("토큰 인증 없이 상품 정보를 조회할 수 있다")
    @Test
    void readsProductWithoutAuthentication() throws Exception {
        Product saved = products.saveAndFlush(
                new Product("상품", 1000, "https://example.com/a"));

        mvc.perform(get("/api/products/{id}", saved.id())).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.id()))
                .andExpect(jsonPath("$.name").value("상품"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/a"));
    }

    @DisplayName("존재하지 않는 상품의 조회 요청은 상품 없음 오류를 반환한다")
    @Test
    void missingProductForQueryReturnsNotFound() throws Exception {
        mvc.perform(get("/api/products/999999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @DisplayName("상품 ID 형식이 잘못되면 입력 오류를 반환한다")
    @Test
    void malformedIdReturnsInputError() throws Exception {
        mvc.perform(get("/api/products/not-a-number")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @DisplayName("상품 목록을 상품 ID 오름차순으로 반환한다")
    @Test
    void returnsProductsInIdOrder() throws Exception {
        Product first = products.saveAndFlush(product("첫째"));
        Product second = products.saveAndFlush(product("둘째"));

        mvc.perform(get("/api/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(first.id()))
                .andExpect(jsonPath("$[1].id").value(second.id()));
    }

    @DisplayName("등록된 상품이 없으면 빈 배열을 반환한다")
    @Test
    void returnsEmptyArrayWhenNoProductsExist() throws Exception {
        mvc.perform(get("/api/products")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @DisplayName("지원하지 않는 응답 형식 요청에는 JSON 오류를 반환한다")
    @Test
    void unsupportedResponseFormatUsesJsonError() throws Exception {
        mvc.perform(get("/api/products").accept("text/plain"))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.code").value("NOT_ACCEPTABLE"));
    }

    @DisplayName("상품 수정에 성공하면 모든 변경 내용을 저장하고 반환한다")
    @Test
    void updatesAndPersistsAllFields() throws Exception {
        Long id = saveOriginalProduct();

        update(id, UPDATE_BODY).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("변경"))
                .andExpect(jsonPath("$.price").value(2000))
                .andExpect(jsonPath("$.imageUrl").value("https://b.com"));

        Product stored = products.findById(id).orElseThrow();
        assertThat(stored.name()).isEqualTo("변경");
        assertThat(stored.price()).isEqualTo(2000);
        assertThat(stored.imageUrl()).isEqualTo("https://b.com");
    }

    @DisplayName("수정 요청의 필수 값이 누락되면 저장된 상품 정보를 유지한다")
    @Test
    void missingFieldsLeaveStoredProductUnchanged() throws Exception {
        Long id = saveOriginalProduct();

        update(id, "{}").andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));

        assertUnchanged(id);
    }

    @DisplayName("수정 요청에 비속어가 있으면 저장된 상품 정보를 유지한다")
    @Test
    void profanityLeavesStoredProductUnchanged() throws Exception {
        Long id = saveOriginalProduct();
        when(checker.containsProfanity(anyString())).thenReturn(true);

        update(id, UPDATE_BODY).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("PROFANITY_DETECTED"));

        assertUnchanged(id);
    }

    @DisplayName("수정 요청의 비속어 검사에 실패하면 저장된 상품 정보를 유지한다")
    @Test
    void unavailableCheckLeavesStoredProductUnchanged() throws Exception {
        Long id = saveOriginalProduct();
        when(checker.containsProfanity(anyString()))
                .thenThrow(new ShoppingException(ErrorCode.PROFANITY_CHECK_UNAVAILABLE));

        update(id, UPDATE_BODY).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("PROFANITY_CHECK_UNAVAILABLE"));

        assertUnchanged(id);
    }

    @DisplayName("존재하지 않는 상품의 수정 요청은 상품 없음 오류를 반환한다")
    @Test
    void missingProductForUpdateReturnsNotFound() throws Exception {
        update(999999L, UPDATE_BODY).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    @DisplayName("요청한 상품만 삭제하고 본문 없는 성공 응답을 반환한다")
    @Test
    void removesOnlyRequestedProductAndReturnsNoContent() throws Exception {
        Product target = products.saveAndFlush(product("대상"));
        Product other = products.saveAndFlush(product("유지"));

        mvc.perform(delete("/api/products/{id}", target.id()))
                .andExpect(status().isNoContent()).andExpect(content().string(""));

        assertThat(products.findAll()).extracting(Product::id).containsExactly(other.id());
    }

    @DisplayName("존재하지 않는 상품의 삭제 요청은 상품 없음 오류를 반환한다")
    @Test
    void missingProductForDeletionReturnsNotFound() throws Exception {
        mvc.perform(delete("/api/products/999999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"));
    }

    private void assertEmpty() {
        assertThat(jdbc.queryForObject("select count(*) from products", Integer.class)).isZero();
    }

    private ResultActions create(String body) throws Exception {
        return mvc.perform(post("/api/products").contentType("application/json").content(body));
    }

    private void assertUnchanged(Long id) {
        Product stored = products.findById(id).orElseThrow();
        assertThat(stored.name()).isEqualTo("기존");
        assertThat(stored.price()).isEqualTo(1000);
        assertThat(stored.imageUrl()).isEqualTo("https://a.com");
    }

    private ResultActions update(Long id, String body) throws Exception {
        return mvc.perform(put("/api/products/{id}", id)
                .contentType("application/json").content(body));
    }

    private Product product(String name) {
        return new Product(name, 1000, "https://example.com/a");
    }

    private Long saveOriginalProduct() {
        return products.saveAndFlush(
                new Product("기존", 1000, "https://a.com")).id();
    }
}
