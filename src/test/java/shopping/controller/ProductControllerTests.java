package shopping.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import shopping.dto.ProductRequest;
import shopping.entity.Product;
import shopping.service.JwtProvider;
import shopping.service.ProductService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTests {

    private static final String IMAGE_URL = "https://api.dicebear.com/10.x/waves/svg";
    private static final ProductRequest REQUEST = new ProductRequest("치킨", 20000, IMAGE_URL);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @DisplayName("상품을 생성하면 201과 Location, 생성된 상품을 응답한다")
    @Test
    void create() throws Exception {
        given(productService.create("치킨", 20000, IMAGE_URL))
                .willReturn(product(1L, "치킨", 20000));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("치킨"))
                .andExpect(jsonPath("$.price").value(20000))
                .andExpect(jsonPath("$.imageUrl").value(IMAGE_URL));
    }

    @DisplayName("상품 목록을 조회한다")
    @Test
    void findAll() throws Exception {
        given(productService.findAll())
                .willReturn(List.of(product(1L, "치킨", 20000), product(2L, "피자", 25000)));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("치킨"))
                .andExpect(jsonPath("$[1].name").value("피자"));
    }

    @DisplayName("상품 하나를 조회한다")
    @Test
    void findById() throws Exception {
        given(productService.findById(1L))
                .willReturn(product(1L, "치킨", 20000));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("치킨"));
    }

    @DisplayName("상품을 수정하면 경로의 id로 서비스를 호출하고 수정된 상품을 응답한다")
    @Test
    void update() throws Exception {
        given(productService.update(1L, "치킨", 20000, IMAGE_URL))
                .willReturn(product(1L, "치킨", 20000));

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.price").value(20000));

        verify(productService).update(1L, "치킨", 20000, IMAGE_URL);
    }

    @DisplayName("상품을 삭제하면 204를 응답한다")
    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).delete(1L);
    }

    private Product product(Long id, String name, int price) {
        Product product = Product.of(name, price, IMAGE_URL);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }
}
