package shopping.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shopping.controller.ProductController;
import shopping.dto.ProductRequest;
import shopping.external.ProfanityCheckException;
import shopping.service.JwtProvider;
import shopping.service.ProductService;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class GlobalExceptionHandlerTests {

    private static final ProductRequest REQUEST =
            new ProductRequest("치킨", 20000, "https://api.dicebear.com/10.x/waves/svg");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @DisplayName("IllegalArgumentException은 400으로 응답한다")
    @Test
    void illegalArgument() throws Exception {
        given(productService.create(anyString(), anyInt(), anyString()))
                .willThrow(new IllegalArgumentException("Product name is too long"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Product name is too long"));
    }

    @DisplayName("NoSuchElementException은 404로 응답한다")
    @Test
    void notFound() throws Exception {
        given(productService.findById(1L))
                .willThrow(new NoSuchElementException("Product not found. id = 1"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Product not found. id = 1"));
    }

    @DisplayName("DuplicateEmailException은 409로 응답한다")
    @Test
    void duplicateEmail() throws Exception {
        given(productService.findById(1L)).willThrow(new DuplicateEmailException());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email already exists"));
    }

    @DisplayName("DuplicateWishException은 409로 응답한다")
    @Test
    void duplicateWish() throws Exception {
        given(productService.findById(1L)).willThrow(new DuplicateWishException("Wish already exists"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Wish already exists"));
    }

    @DisplayName("InvalidCredentialsException은 401로 응답한다")
    @Test
    void invalidCredentials() throws Exception {
        given(productService.findById(1L)).willThrow(new InvalidCredentialsException());

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid email or password"));
    }

    @DisplayName("InvalidTokenException은 401로 응답하고 내부 메시지를 노출하지 않는다")
    @Test
    void invalidToken() throws Exception {
        given(productService.findById(1L)).willThrow(new InvalidTokenException("signature mismatch"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid token"));
    }

    @DisplayName("ProfanityCheckException은 503으로 응답하고 내부 메시지를 노출하지 않는다")
    @Test
    void profanityCheckFailed() throws Exception {
        given(productService.create(anyString(), anyInt(), anyString()))
                .willThrow(new ProfanityCheckException("Failed to check profanity : secret detail"));

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(REQUEST)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("Profanity check is temporarily unavailable"));
    }

    @DisplayName("예상하지 못한 예외는 500으로 응답하고 내부 메시지를 노출하지 않는다")
    @Test
    void unexpected() throws Exception {
        given(productService.findById(anyLong()))
                .willThrow(new IllegalStateException("db password is ..."));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Internal server error"));
    }

    @DisplayName("JSON 형식이 깨져 있으면 400으로 응답한다")
    @Test
    void malformedJson() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content("{invalid"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("필드 타입이 맞지 않으면 400으로 응답한다")
    @Test
    void typeMismatchBody() throws Exception {
        String body = """
                {"name": "치킨", "price": "abc", "imageUrl": "https://a.com/a.png"}
                """;

        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("경로 변수 타입이 맞지 않으면 400으로 응답한다")
    @Test
    void typeMismatchPathVariable() throws Exception {
        mockMvc.perform(get("/api/products/abc"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("지원하지 않는 HTTP 메서드는 405로 응답한다")
    @Test
    void methodNotAllowed() throws Exception {
        mockMvc.perform(patch("/api/products/1"))
                .andExpect(status().isMethodNotAllowed());
    }

    @DisplayName("지원하지 않는 Content-Type은 415로 응답한다")
    @Test
    void unsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.TEXT_PLAIN).content("text"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
