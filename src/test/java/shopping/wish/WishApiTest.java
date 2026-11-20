package shopping.wish;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;
import shopping.wish.entity.Wish;
import shopping.wish.repository.WishRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static shopping.support.ApiTestSupport.assertError;
import static shopping.support.ApiTestSupport.assertInvalidInput;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishApiTest {

    static final String WISHES_URL = "/api/wishes";
    static final String OK_URL = "https://example.com/a.png";

    @Autowired
    MockMvcTester mvc;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WishRepository wishRepository;

    @Autowired
    EntityManager em;

    Member member;
    Product product;
    String token;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("wish@example.com", "hash",
                "위시"));
        product = productRepository.save(new Product("아메리카노", 4000, OK_URL));
        token = jwtProvider.createToken(member.getId());
    }

    @Test
    void 위시에_상품을_추가하면_201과_위시를_준다() {
        // when
        MvcTestResult result = addWish(token, productIdBody(product.getId()));

        // then
        assertThat(result).hasStatus(CREATED);
        assertThat(result).bodyJson().extractingPath("$.id").isNotNull();
        assertThat(result).bodyJson().extractingPath("$.product.id")
                .isEqualTo(product.getId().intValue());
        assertThat(result).bodyJson().extractingPath("$.product.name")
                .isEqualTo("아메리카노");
        assertThat(wishRepository.existsByMemberAndProduct(member, product))
                .isTrue();
    }

    @Test
    void 같은_상품을_두_번_담으면_DUPLICATE_WISH_APPLY_이다() {
        // given
        addWish(token, productIdBody(product.getId()));

        // when
        MvcTestResult result = addWish(token, productIdBody(product.getId()));

        // then
        assertError(result, CONFLICT, "DUPLICATE_WISH_APPLY");
    }

    @Test
    void 없는_상품을_담으면_NOT_EXIST_PRODUCT이다() {
        // when
        MvcTestResult result = addWish(token, productIdBody(999_999L));

        // then
        assertError(result, NOT_FOUND, "NOT_EXIST_PRODUCT");
    }

    @Test
    void 상품_id가_없으면_PRODUCT_ID_MISSING이다() {
        // when
        MvcTestResult result = addWish(token, "{}");

        // then
        assertInvalidInput(result, "PRODUCT_ID_MISSING");
    }

    @Test
    void 토큰의_회원이_없으면_INVALID_TOKEN이다() {
        // given
        String unknownMemberToken = unknownMemberToken();

        // when
        MvcTestResult result = addWish(unknownMemberToken, productIdBody(product.getId()));

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void 위시에_담긴_상품을_삭제하면_위시도_삭제된다() {
        // given
        Wish wish = wishRepository.save(new Wish(member, product));
        em.flush();
        em.clear();

        // when
        MvcTestResult result = mvc.delete().uri("/api/products/{productId}",
                product.getId())
                .exchange();

        em.flush();
        em.clear();

        // then
        assertThat(result).hasStatus(NO_CONTENT);
        assertThat(wishRepository.findById(wish.getId())).isEmpty();
    }

    @Test
    void 위시를_삭제하면_204이고_DB에서_사라진다() {
        // given
        Wish wish = wishRepository.save(new Wish(member, product));

        // when
        MvcTestResult result = deleteWish(token, wish.getId());

        // then
        assertThat(result).hasStatus(NO_CONTENT);
        assertThat(wishRepository.findById(wish.getId())).isEmpty();
    }

    @Test
    void 없는_위시를_삭제하면_NOT_EXIST_WISH이다() {
        // when
        MvcTestResult result = deleteWish(token, 999_999L);

        // then
        assertError(result, NOT_FOUND, "NOT_EXIST_WISH");
    }

    @Test
    void 다른_회원의_위시를_삭제하면_NOT_EXIST_WISH이고_지워지지_않는다() {
        // given
        Member otherMember = saveOtherMember();

        Wish otherWish = wishRepository.save(new Wish(otherMember, product));

        // when
        MvcTestResult result = deleteWish(token, otherWish.getId());

        // then
        assertError(result, NOT_FOUND, "NOT_EXIST_WISH");
        assertThat(wishRepository.findById(otherWish.getId())).isPresent();
    }

    @Test
    void 없는_회원의_토큰으로_위시를_삭제하면_INVALID_TOKEN이다() {
        // given
        Wish wish = wishRepository.save(new Wish(member, product));
        String unknownMemberToken = unknownMemberToken();

        // when
        MvcTestResult result = deleteWish(unknownMemberToken, wish.getId());

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    @Test
    void 위시_목록을_조회하면_200과_내_위시를_준다() {
        // given
        Product latte = saveLatte();
        wishRepository.save(new Wish(member, product));
        wishRepository.save(new Wish(member, latte));

        // when
        MvcTestResult result = getWishes(token);

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).bodyJson().extractingPath("$[*].product.name").asArray()
                .containsExactlyInAnyOrder("아메리카노", "라떼");
    }

    @Test
    void 다른_회원의_위시는_목록에_나오지_않는다() {
        // given
        Member otherMember = saveOtherMember();
        Product latte = saveLatte();
        wishRepository.save(new Wish(member, product));
        wishRepository.save(new Wish(otherMember, latte));

        // when
        MvcTestResult result = getWishes(token);

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).bodyJson().extractingPath("$[*].product.name").asArray()
                .containsExactly("아메리카노");
    }

    @Test
    void 위시가_없으면_빈_목록을_준다() {
        // when
        MvcTestResult result = getWishes(token);

        // then
        assertThat(result).hasStatus(OK);
        assertThat(result).bodyJson().extractingPath("$").asArray()
                .isEmpty();
    }

    @Test
    void 없는_회원의_토큰으로_위시를_조회하면_INVALID_TOKEN이다() {
        // given
        String unknownMemberToken = unknownMemberToken();

        // when
        MvcTestResult result = getWishes(unknownMemberToken);

        // then
        assertError(result, UNAUTHORIZED, "INVALID_TOKEN");
    }

    MvcTestResult addWish(String token, String body) {
        return mvc.post().uri(WISHES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(body)
                .exchange();
    }

    String productIdBody(Long productId) {
        return """
               {"productId": %d}
               """.formatted(productId);
    }

    MvcTestResult deleteWish(String token, Long wishId) {
        return mvc.delete().uri(WISHES_URL + "/{wishId}", wishId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange();
    }

    MvcTestResult getWishes(String token) {
        return mvc.get().uri(WISHES_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange();
    }

    String unknownMemberToken() {
        return jwtProvider.createToken(999_999L);
    }

    Member saveOtherMember() {
        return memberRepository.save(new Member("other@example.com", "hash", "남"));
    }

    Product saveLatte() {
        return productRepository.save(new Product("라떼", 4500, OK_URL));
    }
}
