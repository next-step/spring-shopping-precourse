package shopping.product.web;

import java.math.BigDecimal;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.product.application.InvalidProductException;
import shopping.product.application.ProfanityCheckUnavailableException;
import shopping.product.application.RegisterProduct;
import shopping.product.application.RegisterProduct.RegisteredProduct;
import shopping.product.domain.Product;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final RegisterProduct registerProduct;

    public ProductController(RegisterProduct registerProduct) {
        this.registerProduct = registerProduct;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        RegisteredProduct registered =
                registerProduct.register(request.name(), request.price(), request.imageUrl());
        Product product = registered.product();
        URI location = URI.create("/api/products/" + registered.id());
        ProductResponse response = new ProductResponse(
                registered.id(), product.name().name(), product.price(), product.imageUrl());
        return ResponseEntity.created(location).body(response);
    }

    @ExceptionHandler(InvalidProductException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidProduct(InvalidProductException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequestBody() {
        return new ErrorResponse("상품 등록 요청의 JSON 형식과 필드 값을 확인해 주세요.");
    }

    @ExceptionHandler(ProfanityCheckUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse profanityCheckUnavailable() {
        return new ErrorResponse("비속어 검사를 완료하지 못했습니다. 잠시 후 다시 시도해 주세요.");
    }

    public record ProductRequest(String name, BigDecimal price, String imageUrl) {}

    public record ProductResponse(long id, String name, BigDecimal price, String imageUrl) {}

    public record ErrorResponse(String message) {}
}
