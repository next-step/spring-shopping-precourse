package shopping.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static shopping.support.ApiTestSupport.assertError;
import static shopping.support.ApiTestSupport.assertInvalidInput;
import static shopping.support.ApiTestSupport.postJson;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductApiTest {

    static final String PRODUCTS_URL = "/api/products";
    static final String OK_URL = "https://example.com/a.png";

    @Autowired
    MockMvcTester mvc;

    @Autowired
    ProductRepository productRepository;

    MvcTestResult createProduct(String name, long price, String url) {
        return postJson(mvc, PRODUCTS_URL, productBody(name, price, url));
    }

    MvcTestResult updateProduct(Long productId, String name, long price, String url) {
        return mvc.put().uri(PRODUCTS_URL + "/{productId}", productId)
                .contentType(APPLICATION_JSON)
                .content(productBody(name, price, url))
                .exchange();
    }

    MvcTestResult getProduct(Long productId) {
        return mvc.get().uri(PRODUCTS_URL + "/{productId}", productId).exchange();
    }

    MvcTestResult deleteProduct(Long productId) {
        return mvc.delete().uri(PRODUCTS_URL + "/{productId}", productId).exchange();
    }

    String productBody(String name, long price, String url) {
        return """
                {"name": "%s", "price": %d, "url": "%s"}
                """.formatted(name, price, url);
    }

    Product saveAmericano() {
        return productRepository.save(new Product("아메리카노", 4000, OK_URL));
    }

    @Test
    void 상품을_만들면_201과_저장한_상품을_준다() {
        // when
        MvcTestResult result = createProduct("아이스 아메리카노", 4500, OK_URL);

        // then
        assertThat(result).hasStatus(CREATED);
        Product product = productRepository.findAll().getFirst();
        assertThat(result).bodyJson().extractingPath("$.id")
                .isEqualTo(product.getId().intValue());
        assertThat(result).bodyJson().extractingPath("$.name")
                .isEqualTo("아이스 아메리카노");
        assertThat(product.getPrice()).isEqualTo(4500);
    }

    @Test
    void 허용한_특수_문자와_가격_경계값으로_만들_수_있다() {
        // when
        String name = "()[]+-&/_ 가a1";
        MvcTestResult result = createProduct(name, Integer.MAX_VALUE, "https://example.com/b.png");

        // then
        assertThat(result).hasStatus(CREATED);
        assertThat(result).bodyJson().extractingPath("$.name")
                .isEqualTo(name);
    }

    @Test
    void 값이_없으면_필드마다_누락_코드를_준다() {
        // when
        MvcTestResult result = postJson(mvc, PRODUCTS_URL, "{}");

        // then
        assertInvalidInput(result, "PRODUCT_NAME_MISSING",
                "PRICE_MISSING", "URL_MISSING");
    }

    @Test
    void 이름이_15자를_넘으면_PRODUCT_NAME_TOO_LONG이다() {
        // when
        MvcTestResult result = createProduct("가".repeat(16), 1000, OK_URL);

        // then
        assertInvalidInput(result, "PRODUCT_NAME_TOO_LONG");
    }

    @Test
    void 허용하지_않은_특수_문자는_NOT_ALLOWED_CHARACTERS이다() {
        // when
        MvcTestResult result = createProduct("커피!", 1000, OK_URL);

        // then
        assertInvalidInput(result, "NOT_ALLOWED_CHARACTERS");
    }

    @Test
    void 가격이_음수면_EXCEED_PRICE이다() {
        // when
        MvcTestResult result = createProduct("커피", -1, OK_URL);

        // then
        assertInvalidInput(result, "EXCEED_PRICE");
    }

    @Test
    void 가격이_int_최대값을_넘으면_EXCEED_PRICE이다() {
        // when
        MvcTestResult result = createProduct("커피", 2_147_483_648L, OK_URL);

        // then
        assertInvalidInput(result, "EXCEED_PRICE");
    }

    @Test
    void URL이_http로_시작하지_않으면_INVALID_URL_FORMAT이다() {
        // when
        MvcTestResult result = createProduct("커피", 1000, "example.com/a.png");

        // then
        assertInvalidInput(result, "INVALID_URL_FORMAT");
    }

    @Test
    void 비속어가_있으면_FORBIDDEN_PRODUCT_NAME이고_저장하지_않는다() {
        // when
        MvcTestResult result = createProduct("shit coffee", 1000, OK_URL);

        // then
        assertError(result, BAD_REQUEST, "FORBIDDEN_PRODUCT_NAME");
        assertThat(productRepository.count()).isZero();
    }

    @Test
    void 형식이_틀린_이름은_비속어_검사_전에_거절한다() {
        // when
        MvcTestResult result = createProduct("shit!", 1000, OK_URL);

        // then
        assertInvalidInput(result, "NOT_ALLOWED_CHARACTERS");
    }

    @Test
    void 상품_목록을_조회하면_200과_모든_상품을_준다() {
        // given
        saveAmericano();
        productRepository.save(new Product("라떼", 4500, OK_URL));

        // when
        MvcTestResult result = mvc.get().uri(PRODUCTS_URL).exchange();

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).bodyJson().extractingPath("$[*].name").asArray()
                .containsExactlyInAnyOrder("아메리카노", "라떼");
    }

    @Test
    void 상품이_없으면_빈_목록을_준다() {
        // when
        MvcTestResult result = mvc.get().uri(PRODUCTS_URL).exchange();

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).bodyJson().extractingPath("$").asArray().isEmpty();
    }

    @Test
    void 상품을_조회하면_200과_상품을_준다() {
        // given
        Product product = saveAmericano();

        // when
        MvcTestResult result = getProduct(product.getId());

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).bodyJson().extractingPath("$.name")
                .isEqualTo("아메리카노");
        assertThat(result).bodyJson().extractingPath("$.price")
                .isEqualTo(4000);
    }

    @Test
    void 없는_상품을_조회하면_NOT_EXIST_PRODUCT이다() {
        // when
        MvcTestResult result = getProduct(999_999L);

        // then
        assertError(result, NOT_FOUND, "NOT_EXIST_PRODUCT");
    }

    @Test
    void 상품을_수정하면_204이고_바뀐_값이_저장된다() {
        // given
        Product product = saveAmericano();

        // when
        MvcTestResult result = updateProduct(
                product.getId(), "라떼", 4500, "https://example.com/latte.png");

        // then
        assertThat(result).hasStatus(NO_CONTENT);
        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();

        assertThat(foundProduct.getName()).isEqualTo("라떼");
        assertThat(foundProduct.getPrice()).isEqualTo(4500);
        assertThat(foundProduct.getUrl()).isEqualTo("https://example.com/latte.png");
    }

    @Test
    void 없는_상품을_수정하면_NOT_EXIST_PRODUCT이다() {
        // when
        MvcTestResult result = updateProduct(999_999L, "라떼", 4500, OK_URL);

        // then
        assertError(result, NOT_FOUND, "NOT_EXIST_PRODUCT");
    }

    @Test
    void 수정할_이름에_비속어가_있으면_FORBIDDEN_PRODUCT_NAME이고_바뀌지_않는다() {
        // given
        Product product = saveAmericano();

        // when
        MvcTestResult result = updateProduct(product.getId(), "shit coffee", 4500, OK_URL);

        // then
        assertError(result, BAD_REQUEST, "FORBIDDEN_PRODUCT_NAME");
        assertThat(productRepository.findById(product.getId()).orElseThrow().getName())
                .isEqualTo("아메리카노");
    }

    @Test
    void 수정_값의_형식이_틀리면_INVALID_INPUT이다() {
        // given
        Product product = saveAmericano();

        // when
        MvcTestResult result = updateProduct(product.getId(), "가".repeat(16), -1, "abc");

        // then
        assertInvalidInput(result, "PRODUCT_NAME_TOO_LONG",
                "EXCEED_PRICE", "INVALID_URL_FORMAT");
    }

    @Test
    void 상품을_삭제하면_204이고_DB에서_사라진다() {
        // given
        Product product = saveAmericano();

        // when
        MvcTestResult result = deleteProduct(product.getId());

        // then
        assertThat(result).hasStatus(NO_CONTENT);
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    void 없는_상품을_삭제하면_NOT_EXIST_PRODUCT이다() {
        // when
        MvcTestResult result = deleteProduct(999_999L);

        // then
        assertError(result, NOT_FOUND, "NOT_EXIST_PRODUCT");
    }
}
