package shopping.product.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import shopping.auth.JwtTokenProvider;
import shopping.product.domain.Product;
import shopping.product.exception.InvalidProductNameException;
import shopping.product.exception.ProductNotFoundException;
import shopping.product.exception.ProfanityCheckUnavailableException;
import shopping.product.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    private static final String IMAGE_URL = "https://image.com/product.png";
    private static final String VALID_BODY =
            "{\"name\": \"컵\", \"price\": 3000, \"imageUrl\": \"" + IMAGE_URL + "\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void 상품을_등록하면_201과_등록된_상품을_돌려준다() throws Exception {
        given(productService.create("컵", 3000, IMAGE_URL)).willReturn(createProduct());

        postProduct(VALID_BODY)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("컵"))
                .andExpect(jsonPath("$.price").value(3000))
                .andExpect(jsonPath("$.imageUrl").value(IMAGE_URL));
    }

    @Test
    void 목록을_조회하면_200과_상품_목록을_돌려준다() throws Exception {
        given(productService.findAll()).willReturn(List.of(createProduct()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("컵"));
    }

    @Test
    void 없는_상품을_조회하면_404와_메시지를_돌려준다() throws Exception {
        given(productService.findById(999L))
                .willThrow(new ProductNotFoundException("상품을 찾을 수 없습니다."));
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
    }

    @Test
    void 이미지_URL이_없으면_400이고_서비스는_호출되지_않는다() throws Exception {
        postProduct("{\"name\": \"컵\", \"price\": 3000}")
                .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void 상품을_수정하면_200과_수정된_상품을_돌려준다() throws Exception {
        given(productService.update(1L, "컵", 3000, IMAGE_URL)).willReturn(createProduct());

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void 상품을_삭제하면_204를_돌려준다() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }

    @Test
    void 비속어_검사를_할_수_없으면_503과_메시지를_돌려준다() throws Exception {
        given(productService.create(anyString(), anyInt(), anyString()))
                .willThrow(new ProfanityCheckUnavailableException("비속어 검사 서비스를 사용할 수 없습니다."));

        postProduct(VALID_BODY)
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("비속어 검사 서비스를 사용할 수 없습니다."));
    }

    @Test
    void 이름_규칙에_어긋나면_400과_메시지를_돌려준다() throws Exception {
        given(productService.create(anyString(), anyInt(), anyString()))
                .willThrow(new InvalidProductNameException("상품 이름은 15자 이하여야 합니다."));

        postProduct(VALID_BODY)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 이름은 15자 이하여야 합니다."));
    }

    private ResultActions postProduct(String body) throws Exception {
        return mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private Product createProduct() {
        return new Product(1L, "컵", 3000, IMAGE_URL);
    }
}
