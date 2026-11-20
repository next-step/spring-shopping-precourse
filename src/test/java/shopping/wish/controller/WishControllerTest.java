package shopping.wish.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtProvider jwtProvider;
    @Autowired ProductRepository productRepository;

    private String bearer(Long memberId) {
        return "Bearer " + jwtProvider.createToken(memberId, "test@test.com");
    }

    private Long saveProduct() {
        return productRepository.save(new Product("돼지뚱냥이", 1000, 1, "https://i.ibb.co/a.jpg")).getId();
    }

    private void addWish(Long memberId, Long productId) throws Exception {
        mockMvc.perform(post("/api/wishes")
                        .header(HttpHeaders.AUTHORIZATION, bearer(memberId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void 위시_추가_201() throws Exception {
        Long productId = saveProduct();

        mockMvc.perform(post("/api/wishes")
                        .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId));
    }

    @Test
    void 토큰이_없으면_401() throws Exception {
        mockMvc.perform(get("/api/wishes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void 잘못된_토큰이면_401() throws Exception {
        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, "Bearer abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 없는_상품이면_404() throws Exception {
        mockMvc.perform(post("/api/wishes")
                        .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":999}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 이미_담은_상품이면_409() throws Exception {
        Long productId = saveProduct();
        addWish(1L, productId);

        mockMvc.perform(post("/api/wishes")
                        .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":" + productId + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void 위시_목록_조회_200() throws Exception {
        addWish(1L, saveProduct());

        mockMvc.perform(get("/api/wishes").header(HttpHeaders.AUTHORIZATION, bearer(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("돼지뚱냥이"));
    }

    @Test
    void 없는_위시_삭제시_404() throws Exception {
        mockMvc.perform(delete("/api/wishes/999").header(HttpHeaders.AUTHORIZATION, bearer(1L)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 삭제시_토큰이_없으면_401() throws Exception {
        mockMvc.perform(delete("/api/wishes/1"))
                .andExpect(status().isUnauthorized());
    }
}
