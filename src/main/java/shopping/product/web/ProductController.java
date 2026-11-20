package shopping.product.web;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import shopping.product.application.InvalidProductException;
import shopping.product.application.ProductNotFoundException;
import shopping.product.application.ProductRepository.StoredProduct;
import shopping.product.application.ProductService;
import shopping.product.application.ProfanityCheckUnavailableException;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        StoredProduct registered =
                productService.register(request.name(), request.price(), request.imageUrl());
        URI location = URI.create("/api/products/" + registered.id());
        return ResponseEntity.created(location).body(ProductResponse.from(registered));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProductResponse> findAll() {
        return productService.findAll().stream().map(ProductResponse::from).toList();
    }

    @GetMapping(path = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse findById(@PathVariable("productId") long productId) {
        return ProductResponse.from(productService.findById(productId));
    }

    @PutMapping(path = "/{productId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ProductResponse update(
            @PathVariable("productId") long productId, @RequestBody ProductRequest request) {
        StoredProduct updated =
                productService.update(productId, request.name(), request.price(), request.imageUrl());
        return ProductResponse.from(updated);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("productId") long productId) {
        productService.delete(productId);
    }

    @ExceptionHandler(InvalidProductException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidProduct(InvalidProductException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequestBody() {
        return new ErrorResponse("상품 요청의 JSON 형식과 필드 값을 확인해 주세요.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidProductId() {
        return new ErrorResponse("상품 ID는 정수여야 합니다.");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse productNotFound(ProductNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(ProfanityCheckUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse profanityCheckUnavailable() {
        return new ErrorResponse("비속어 검사를 완료하지 못했습니다. 잠시 후 다시 시도해 주세요.");
    }

    public record ProductRequest(String name, BigDecimal price, String imageUrl) {}

    public record ProductResponse(long id, String name, BigDecimal price, String imageUrl) {
        private static ProductResponse from(StoredProduct product) {
            return new ProductResponse(product.id(), product.name(), product.price(), product.imageUrl());
        }
    }

    public record ErrorResponse(String message) {}
}
