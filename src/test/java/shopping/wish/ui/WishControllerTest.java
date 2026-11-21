package shopping.wish.ui;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtTokenProvider;
import shopping.member.domain.Member;
import shopping.member.domain.MemberRepository;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.wish.domain.Wish;
import shopping.wish.domain.WishRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishRepository wishRepository;

    private Member member;
    private Product product;
    private String authorization;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("user@test.com", "password"));
        product = productRepository.save(
                new Product("상품", 1_000L, "https://example.com/product.jpg"));
        authorization = "Bearer " + tokenProvider.createToken(member.getId());
    }

    @Test
    @DisplayName("POST /api/wishes로 위시 리스트에 상품을 추가한다")
    void add() {
        String body = "{\"productId\": " + product.getId() + "}";

        MvcTestResult result = mockMvc.post().uri("/api/wishes")
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.productId")
                .isEqualTo(product.getId().intValue());
        assertThat(wishRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 상품을 위시에 추가하면 예외가 발생한다")
    void addUnknownProduct() {
        MvcTestResult result = addWish("{\"productId\": 999}");

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("중복 상품을 위시에 추가하면 예외가 발생한다")
    void addDuplicateProduct() {
        String body = "{\"productId\": " + product.getId() + "}";
        addWish(body);

        MvcTestResult result = addWish(body);

        assertThat(result).hasStatus(HttpStatus.CONFLICT);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("GET /api/wishes로 회원의 위시 상품을 조회한다")
    void findAll() {
        Wish wish = wishRepository.save(new Wish(member, product));

        MvcTestResult result = mockMvc.get().uri("/api/wishes")
                .header("Authorization", authorization)
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$[0].id")
                .isEqualTo(wish.getId().intValue());
        assertThat(result).bodyJson().extractingPath("$[0].name").isEqualTo("상품");
    }

    @Test
    @DisplayName("DELETE /api/wishes/{wishId}로 위시 상품을 삭제한다")
    void delete() {
        Wish wish = wishRepository.save(new Wish(member, product));

        MvcTestResult result = mockMvc.delete().uri("/api/wishes/{wishId}", wish.getId())
                .header("Authorization", authorization)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(wishRepository.existsById(wish.getId())).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 위시를 삭제하면 예외가 발생한다")
    void deleteUnknownWish() {
        MvcTestResult result = mockMvc.delete().uri("/api/wishes/{wishId}", 999L)
                .header("Authorization", authorization)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("다른 회원의 위시를 삭제하면 예외가 발생한다")
    void deleteOtherMembersWish() {
        Member other = memberRepository.save(new Member("other@test.com", "password"));
        Wish wish = wishRepository.save(new Wish(other, product));

        MvcTestResult result = mockMvc.delete().uri("/api/wishes/{wishId}", wish.getId())
                .header("Authorization", authorization)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @Test
    @DisplayName("인증 토큰 없이 위시 리스트를 요청하면 예외가 발생한다")
    void requestWithoutToken() {
        MvcTestResult result = mockMvc.get().uri("/api/wishes").exchange();

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    @ParameterizedTest
    @ValueSource(strings = {"token", "Basic token", "Bearer invalid.token"})
    @DisplayName("유효하지 않은 인증 토큰으로 위시 리스트를 요청하면 예외가 발생한다")
    void requestWithInvalidToken(String invalidAuthorization) {
        MvcTestResult result = mockMvc.get().uri("/api/wishes")
                .header("Authorization", invalidAuthorization)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.UNAUTHORIZED);
        assertThat(result).bodyJson().extractingPath("$.message").asString().isNotBlank();
    }

    private MvcTestResult addWish(String body) {
        return mockMvc.post().uri("/api/wishes")
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .exchange();
    }
}
