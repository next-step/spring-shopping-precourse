package shopping.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import shopping.dto.WishRequest;
import shopping.entity.Member;
import shopping.entity.PasswordEncoder;
import shopping.entity.Product;
import shopping.entity.Wish;
import shopping.exception.DuplicateWishException;
import shopping.exception.InvalidTokenException;
import shopping.service.JwtProvider;
import shopping.service.WishService;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishController.class)
class WishControllerTests {

    private static final String BEARER_TOKEN = "Bearer token";
    private static final Long MEMBER_ID = 1L;
    private static final Long PRODUCT_ID = 10L;
    private static final Long WISH_ID = 100L;
    private static final String IMAGE_URL = "https://api.dicebear.com/10.x/waves/svg";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WishService wishService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        given(jwtProvider.getMemberId("token")).willReturn(MEMBER_ID);
    }

    static Stream<MockHttpServletRequestBuilder> wishRequests() {
        return Stream.of(
                post("/api/wishes").contentType(MediaType.APPLICATION_JSON).content("{\"productId\": 10}"),
                get("/api/wishes"),
                delete("/api/wishes/100")
        );
    }

    @DisplayName("위시를 추가하면 인증된 회원의 id로 서비스를 호출하고 201과 위시를 응답한다")
    @Test
    void add() throws Exception {
        Wish wish = wish(WISH_ID, PRODUCT_ID, "치킨", 20000);
        given(wishService.add(MEMBER_ID, PRODUCT_ID)).willReturn(wish);

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WishRequest(PRODUCT_ID))))
                .andExpect(status().isCreated())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.product.id").value(10))
                .andExpect(jsonPath("$.product.name").value("치킨"))
                .andExpect(jsonPath("$.product.price").value(20000))
                .andExpect(jsonPath("$.product.imageUrl").value(IMAGE_URL));
        verify(wishService).add(MEMBER_ID, PRODUCT_ID);
    }

    @DisplayName("상품 id가 없으면 400을 응답하고 서비스를 호출하지 않는다")
    @Test
    void addWithoutProductId() throws Exception {
        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WishRequest(null))))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(wishService);
    }

    @DisplayName("이미 담은 상품이면 409로 응답한다")
    @Test
    void addDuplicate() throws Exception {
        given(wishService.add(MEMBER_ID, PRODUCT_ID)).willThrow(new DuplicateWishException("Wish already exists"));

        mockMvc.perform(post("/api/wishes")
                        .header("Authorization", BEARER_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WishRequest(PRODUCT_ID))))
                .andExpect(status().isConflict());
    }

    @DisplayName("위시 목록을 인증된 회원의 id로 조회한다")
    @Test
    void findAll() throws Exception {
        List<Wish> wishes = List.of(
                wish(100L, 10L, "치킨", 20000),
                wish(101L, 11L, "피자", 25000)
        );
        given(wishService.findAll(MEMBER_ID)).willReturn(wishes);

        mockMvc.perform(get("/api/wishes")
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].product.name").value("치킨"))
                .andExpect(jsonPath("$[1].id").value(101))
                .andExpect(jsonPath("$[1].product.name").value("피자"));
    }

    @DisplayName("위시를 삭제하면 인증된 회원의 id로 서비스를 호출하고 204를 응답한다")
    @Test
    void deleteWish() throws Exception {
        mockMvc.perform(delete("/api/wishes/" + WISH_ID)
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isNoContent());
        verify(wishService).delete(MEMBER_ID, WISH_ID);
    }

    @DisplayName("존재하지 않거나 다른 회원의 위시를 삭제하면 404로 응답한다")
    @Test
    void deleteNotFound() throws Exception {
        doThrow(new NoSuchElementException("Wish not found. id = 100"))
                .when(wishService).delete(MEMBER_ID, WISH_ID);

        mockMvc.perform(delete("/api/wishes/" + WISH_ID)
                        .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Authorization 헤더가 없으면 401을 응답하고 서비스를 호출하지 않는다")
    @ParameterizedTest
    @MethodSource("wishRequests")
    void withoutAuthorizationHeader(MockHttpServletRequestBuilder request) throws Exception {
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(wishService);
    }

    @DisplayName("유효하지 않은 토큰이면 401을 응답하고 서비스를 호출하지 않는다")
    @ParameterizedTest
    @MethodSource("wishRequests")
    void withInvalidToken(MockHttpServletRequestBuilder request) throws Exception {
        given(jwtProvider.getMemberId("invalid"))
                .willThrow(new InvalidTokenException("Invalid token"));

        mockMvc.perform(request.header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(wishService);
    }

    private Wish wish(Long wishId, Long productId, String productName, int price) {
        Member member = Member.of("user@example.com", "password123", mock(PasswordEncoder.class));
        Product product = Product.of(productName, price, IMAGE_URL);
        ReflectionTestUtils.setField(product, "id", productId);
        Wish wish = Wish.of(member, product);
        ReflectionTestUtils.setField(wish, "id", wishId);
        return wish;
    }
}
