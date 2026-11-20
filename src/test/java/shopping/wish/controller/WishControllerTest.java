package shopping.wish.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.JwtException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.auth.JwtTokenProvider;
import shopping.product.domain.Product;
import shopping.product.exception.ProductNotFoundException;
import shopping.wish.domain.Wish;
import shopping.wish.exception.DuplicateWishException;
import shopping.wish.exception.WishAccessDeniedException;
import shopping.wish.exception.WishNotFoundException;
import shopping.wish.service.WishService;

@WebMvcTest(WishController.class)
class WishControllerTest {
    private static final String TOKEN = "valid-token";
    private static final String AUTHORIZATION = "Bearer " + TOKEN;
    private static final Long MEMBER_ID = 1L;
    private static final String BODY = "{\"productId\": 10}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishService wishService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        given(jwtTokenProvider.extractMemberId(TOKEN)).willReturn(MEMBER_ID);
    }

    @Test
    void 상품을_담으면_201과_위시를_돌려준다() throws Exception {
        given(wishService.add(MEMBER_ID, 10L)).willReturn(createWish());

        postWish(BODY)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.product.id").value(10))
                .andExpect(jsonPath("$.product.name").value("컵"));
    }

    @Test
    void 목록을_조회하면_200과_내_위시를_돌려준다() throws Exception {
        given(wishService.findAll(MEMBER_ID)).willReturn(List.of(createWish()));

        mockMvc.perform(get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].product.name").value("컵"));
    }

    @Test
    void 삭제하면_204를_돌려준다() throws Exception {
        mockMvc.perform(delete("/api/wishes/100")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION))
                .andExpect(status().isNoContent());

        verify(wishService).delete(MEMBER_ID, 100L);
    }

    @Test
    void 토큰이_없으면_401이고_서비스는_호출되지_않는다() throws Exception {
        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(wishService);
    }

    @Test
    void 유효하지_않은_토큰이면_401이다() throws Exception {
        given(jwtTokenProvider.extractMemberId("bad")).willThrow(new JwtException("Invalid"));

        mockMvc.perform(get("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer bad"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."));

        verifyNoInteractions(wishService);
    }

    @Test
    void 상품_id가_없으면_400이고_서비스는_호출되지_않는다() throws Exception {
        postWish("{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 id는 필수입니다."));

        verifyNoInteractions(wishService);
    }

    @Test
    void 없는_상품을_담으면_404와_메시지를_돌려준다() throws Exception {
        given(wishService.add(MEMBER_ID, 10L))
                .willThrow(new ProductNotFoundException(("상품을 찾을 수 없습니다.")));

        postWish(BODY)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }

    @Test
    void 이미_담은_상품이면_409와_메시지를_돌려준다() throws Exception {
        given(wishService.add(MEMBER_ID, 10L))
                .willThrow(new DuplicateWishException("이미 위시리스트에 담은 상품입니다."));

        postWish(BODY)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 위시리스트에 담은 상품입니다."));
    }

    @Test
    void 남의_위시를_삭제하면_403과_메시지를_돌려준다() throws Exception {
        willThrow(new WishAccessDeniedException("본인의 위시만 삭제할 수 있습니다."))
                .given(wishService).delete(MEMBER_ID, 100L);

        mockMvc.perform(delete("/api/wishes/100")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("본인의 위시만 삭제할 수 있습니다."));
    }

    @Test
    void 없는_위시를_삭제하면_404와_메시지를_돌려준다() throws Exception {
        willThrow(new WishNotFoundException("위시를 찾을 수 없습니다."))
                .given(wishService).delete(MEMBER_ID, 100L);

        mockMvc.perform(delete("/api/wishes/100")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("위시를 찾을 수 없습니다."));
    }

    private ResultActions postWish(String body) throws Exception {
        return mockMvc.perform(post("/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private Wish createWish() {
        Product product = new Product(10L, "컵", 3000, "https://image.com/cup.png");
        return new Wish(100L, MEMBER_ID, product);
    }
}