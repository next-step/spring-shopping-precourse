package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import shopping.support.ApiTest;

class WishApiTest extends ApiTest {

    @Autowired
    private EntityManager entityManager;

    private String token;
    private Long productId;

    @BeforeEach
    void setUp() {
        token = registerAndGetToken("user@example.com");
        productId = createProduct("콜라");
    }

    @Test
    void 위시_리스트에_상품을_추가한다() {
        MvcTestResult result = addWish(token, productId);

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("콜라");
        assertThat(result).bodyJson().extractingPath("$.productId").isEqualTo(productId.intValue());
    }

    @Test
    void 위시_리스트에_있는_상품을_조회한다() {
        addWish(token, productId);
        addWish(token, createProduct("사이다"));

        MvcTestResult result = findWishes(token);

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.length()").isEqualTo(2);
    }

    @Test
    void 다른_회원의_위시는_조회되지_않는다() {
        addWish(token, productId);
        String otherToken = registerAndGetToken("other@example.com");

        MvcTestResult result = findWishes(otherToken);

        assertThat(result).bodyJson().extractingPath("$.length()").isEqualTo(0);
    }

    @Test
    void 위시_리스트에서_상품을_삭제한다() {
        Long wishId = readId(addWish(token, productId));

        MvcTestResult result = deleteWish(token, wishId);

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(findWishes(token)).bodyJson().extractingPath("$.length()").isEqualTo(0);
    }

    @Test
    void 존재하지_않는_상품은_추가할_수_없다() {
        assertThat(addWish(token, 999L)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void 이미_담은_상품은_다시_추가할_수_없다() {
        addWish(token, productId);

        assertThat(addWish(token, productId)).hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void 다른_회원의_위시는_삭제할_수_없다() {
        Long wishId = readId(addWish(token, productId));
        String otherToken = registerAndGetToken("other@example.com");

        assertThat(deleteWish(otherToken, wishId)).hasStatus(HttpStatus.FORBIDDEN);
        assertThat(findWishes(token)).bodyJson().extractingPath("$.length()").isEqualTo(1);
    }

    @Test
    void 존재하지_않는_위시는_삭제할_수_없다() {
        assertThat(deleteWish(token, 999L)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void 상품이_삭제되면_위시_리스트에서도_사라진다() {
        addWish(token, productId);
        entityManager.flush();
        entityManager.clear();

        MvcTestResult result = mvc.delete().uri("/api/products/{id}", productId).exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(findWishes(token)).bodyJson().extractingPath("$.length()").isEqualTo(0);
    }

    @Test
    void 토큰이_없으면_401을_응답한다() {
        assertThat(mvc.get().uri("/api/wishes")).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(post("/api/wishes", wishJson(productId))).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.delete().uri("/api/wishes/{id}", 1)).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer invalid-token", "Bearer ", "Basic abc", "invalid-token"})
    void 토큰이_올바르지_않으면_401을_응답한다(String header) {
        MvcTestResult result = mvc.get().uri("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, header)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    void 상품_id가_없으면_400을_응답한다() {
        assertThat(postWithToken("/api/wishes", token, "{}")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    private MvcTestResult addWish(String token, Long productId) {
        return postWithToken("/api/wishes", token, wishJson(productId));
    }

    private MvcTestResult findWishes(String token) {
        return mvc.get().uri("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .exchange();
    }

    private MvcTestResult deleteWish(String token, Long wishId) {
        return mvc.delete().uri("/api/wishes/{id}", wishId)
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .exchange();
    }

    private String wishJson(Long productId) {
        return """
                {"productId": %d}
                """.formatted(productId);
    }
}
