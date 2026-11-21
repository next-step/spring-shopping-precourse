package shopping.wish;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import shopping.auth.AuthTokenRepository;
import shopping.member.MemberRepository;
import shopping.product.ImageUrl;
import shopping.product.Price;
import shopping.product.Product;
import shopping.product.ProductName;
import shopping.product.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
class WishApiTest {

    private static final String INVALID_TOKEN_MESSAGE = "인증 토큰이 없거나 유효하지 않습니다.";

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private WishRepository wishRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        wishRepository.deleteAll();
        authTokenRepository.deleteAll();
        memberRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("인증된 사용자는 자신의 위시 리스트에 상품을 추가할 수 있다")
    void addWish() throws Exception {
        String token = registerAndGetToken("wish@example.com");
        Product product = saveProduct("사과");
        String request = """
                {"productId": %d}
                """.formatted(product.getId());

        assertThat(postWish(request).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.name").isEqualTo("사과");
        Long memberId = memberRepository.findByEmail("wish@example.com").orElseThrow().getId();
        assertThat(wishRepository.findAll())
                .extracting(Wish::getMemberId)
                .containsExactly(memberId);
    }

    @Test
    @DisplayName("토큰 없이 위시 리스트에 접근하면 요청을 거부한다")
    void rejectMissingToken() {
        Product product = saveProduct("사과");
        String request = """
                {"productId": %d}
                """.formatted(product.getId());

        assertThat(postWish(request))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.message").isEqualTo(INVALID_TOKEN_MESSAGE);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 위시 리스트에 접근하면 요청을 거부한다")
    void rejectInvalidToken() {
        Product product = saveProduct("사과");
        String request = """
                {"productId": %d}
                """.formatted(product.getId());

        assertThat(postWish(request).header(HttpHeaders.AUTHORIZATION, bearer("invalid-token")))
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.message").isEqualTo(INVALID_TOKEN_MESSAGE);
    }

    @Test
    @DisplayName("존재하지 않는 상품은 위시 리스트에 추가할 수 없다")
    void rejectUnknownProduct() throws Exception {
        String token = registerAndGetToken("wish@example.com");
        String request = """
                {"productId": 999999}
                """;

        assertThat(postWish(request).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("존재하지 않는 상품입니다.");
        assertThat(wishRepository.count()).isZero();
    }

    @Test
    @DisplayName("이미 추가한 상품을 중복 등록하면 거부한다")
    void rejectDuplicateWish() throws Exception {
        String token = registerAndGetToken("wish@example.com");
        Product product = saveProduct("사과");
        String request = """
                {"productId": %d}
                """.formatted(product.getId());
        postWish(request).header(HttpHeaders.AUTHORIZATION, bearer(token)).exchange();

        assertThat(postWish(request).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("이미 위시 리스트에 추가한 상품입니다.");
        assertThat(wishRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("다른 사용자가 추가한 상품은 내 위시 리스트에 추가할 수 있다")
    void allowSameProductForOtherMember() throws Exception {
        String myToken = registerAndGetToken("me@example.com");
        String otherToken = registerAndGetToken("other@example.com");
        Product product = saveProduct("사과");
        String request = """
                {"productId": %d}
                """.formatted(product.getId());
        postWish(request).header(HttpHeaders.AUTHORIZATION, bearer(otherToken)).exchange();

        assertThat(postWish(request).header(HttpHeaders.AUTHORIZATION, bearer(myToken)))
                .hasStatus(HttpStatus.CREATED);
        assertThat(wishRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("인증된 사용자는 자신의 위시 리스트에 등록된 상품 목록을 조회할 수 있다")
    void findMyWishes() throws Exception {
        String myToken = registerAndGetToken("me@example.com");
        String otherToken = registerAndGetToken("other@example.com");
        addWish(myToken, saveProduct("사과"));
        addWish(myToken, saveProduct("배"));
        addWish(otherToken, saveProduct("수박"));

        assertThat(getWishes().header(HttpHeaders.AUTHORIZATION, bearer(myToken)))
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .extractingPath("$[*].name").asArray()
                .containsExactlyInAnyOrder("사과", "배");
    }

    @Test
    @DisplayName("토큰 없이 위시 리스트를 조회하면 요청을 거부한다")
    void rejectFindWithoutToken() {
        assertThat(getWishes())
                .hasStatus(HttpStatus.UNAUTHORIZED)
                .bodyJson()
                .extractingPath("$.message").isEqualTo(INVALID_TOKEN_MESSAGE);
    }

    @Test
    @DisplayName("인증된 사용자는 위시 리스트 ID로 자신의 위시 리스트 항목을 삭제할 수 있다")
    void deleteMyWish() throws Exception {
        String token = registerAndGetToken("me@example.com");
        Long wishId = addWish(token, saveProduct("사과"));

        assertThat(deleteWish(wishId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .hasStatus(HttpStatus.NO_CONTENT);
        assertThat(wishRepository.findById(wishId)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 위시 리스트 ID로 삭제하면 오류를 반환한다")
    void rejectDeleteUnknownWish() throws Exception {
        String token = registerAndGetToken("me@example.com");

        assertThat(deleteWish(999999L).header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("존재하지 않는 위시 리스트 항목입니다.");
    }

    @Test
    @DisplayName("다른 사용자의 위시 리스트 항목을 삭제할 수 없다")
    void rejectDeleteOtherMembersWish() throws Exception {
        String myToken = registerAndGetToken("me@example.com");
        String otherToken = registerAndGetToken("other@example.com");
        Long otherWishId = addWish(otherToken, saveProduct("수박"));

        assertThat(deleteWish(otherWishId).header(HttpHeaders.AUTHORIZATION, bearer(myToken)))
                .hasStatus(HttpStatus.FORBIDDEN)
                .bodyJson()
                .extractingPath("$.message").isEqualTo("다른 사용자의 위시 리스트 항목은 삭제할 수 없습니다.");
        assertThat(wishRepository.findById(otherWishId)).isPresent();
    }

    private Long addWish(String token, Product product) throws Exception {
        String request = """
                {"productId": %d}
                """.formatted(product.getId());
        String response = postWish(request)
                .header(HttpHeaders.AUTHORIZATION, bearer(token))
                .exchange()
                .getResponse()
                .getContentAsString();
        return JsonPath.parse(response).read("$.id", Long.class);
    }

    private String registerAndGetToken(String email) throws Exception {
        String request = """
                {"email": "%s", "password": "password123"}
                """.formatted(email);
        String response = mockMvc.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(response, "$.token");
    }

    private Product saveProduct(String name) {
        return productRepository.save(new Product(
                new ProductName(name), new Price(1000), new ImageUrl("https://example.com/item.png")));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private MockMvcTester.MockMvcRequestBuilder postWish(String body) {
        return mockMvc.post()
                .uri("/api/wishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    private MockMvcTester.MockMvcRequestBuilder getWishes() {
        return mockMvc.get().uri("/api/wishes");
    }

    private MockMvcTester.MockMvcRequestBuilder deleteWish(Long wishId) {
        return mockMvc.delete().uri("/api/wishes/{wishId}", wishId);
    }
}
