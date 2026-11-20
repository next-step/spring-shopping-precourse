package shopping.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import shopping.PurgoMalumClient;
import shopping.domain.Wish;
import shopping.dto.CreateProductRequest;
import shopping.dto.MemberRequest;
import shopping.service.MemberService;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

class WishControllerTest {
    private final MemberService members = new MemberService();
    private final ProductController products = new ProductController(mock(PurgoMalumClient.class));
    private final MockMvcTester mvc = MockMvcTester.of(new WishController(members, products));
    private final JsonMapper json = new JsonMapper();

    @Test
    void 같은_회원의_중복_찜은_거절하고_다른_회원의_목록은_비어_있다() {
        String alice = signUp("alice@example.com");
        String bob = signUp("bob@example.com");
        long productId = createProduct();
        Wish wish = addWish(alice, productId);

        assertThat(add(alice, productId)).hasStatus(HttpStatus.CONFLICT);
        assertWishes(alice, wish);
        assertWishes(bob);
    }

    @Test
    void 같은_상품을_찜해도_자기_항목만_삭제할_수_있다() {
        String alice = signUp("alice@example.com");
        String bob = signUp("bob@example.com");
        long productId = createProduct();
        Wish bobWish = addWish(bob, productId);
        Wish aliceWish = addWish(alice, productId);

        assertThat(delete(bob, aliceWish)).hasStatus(HttpStatus.NOT_FOUND);
        assertWishes(alice, aliceWish);
        assertThat(delete(alice, aliceWish)).hasStatus(HttpStatus.NO_CONTENT).bodyText().isEmpty();
        assertWishes(alice);
        assertWishes(bob, bobWish);
        assertThat(delete(alice, aliceWish)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void 없는_상품은_찜에_추가할_수_없다() {
        String alice = signUp("alice@example.com");

        assertThat(add(alice, 99L)).hasStatus(HttpStatus.NOT_FOUND);
        assertWishes(alice);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = "Bearer invalid")
    void 토큰이_없거나_잘못되면_조회와_추가와_삭제를_거절한다(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        if (authorization != null) {
            headers.set(AUTHORIZATION, authorization);
        }

        assertThat(mvc.get().uri("/api/wishes").headers(headers))
                .hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.post().uri("/api/wishes").headers(headers).contentType(APPLICATION_JSON)
                .content("{\"productId\":1}")).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(mvc.delete().uri("/api/wishes/1").headers(headers))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    private String signUp(String email) {
        return "Bearer " + members.register(new MemberRequest(email, "sample-password")).token();
    }

    private long createProduct() {
        var request = new CreateProductRequest("머그컵", 12000, "https://example.com/mug.png");
        return products.createProducts(request).getProductId();
    }

    private MvcTestResult add(String token, long productId) {
        return mvc.post().uri("/api/wishes").header(AUTHORIZATION, token)
                .contentType(APPLICATION_JSON)
                .content("{\"productId\":" + productId + "}").exchange();
    }

    private Wish addWish(String token, long productId) {
        MvcTestResult result = add(token, productId);
        assertThat(result).hasStatus(HttpStatus.CREATED);
        byte[] body = result.getMvcResult().getResponse().getContentAsByteArray();
        Wish wish = json.readValue(body, Wish.class);
        assertThat(wish.productId()).isEqualTo(productId);
        return wish;
    }

    private MvcTestResult delete(String token, Wish wish) {
        return mvc.delete().uri("/api/wishes/{id}", wish.wishId()).header(AUTHORIZATION, token)
                .exchange();
    }

    private void assertWishes(String token, Wish... expected) {
        assertThat(mvc.get().uri("/api/wishes").header(AUTHORIZATION, token)).hasStatusOk()
                .bodyJson().isStrictlyEqualTo(json.writeValueAsString(expected));
    }

}