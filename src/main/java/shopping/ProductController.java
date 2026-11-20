package shopping;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductCreateRequest request) {
        Product product =
                productService.create(request.name(), request.price(), request.imageUrl());
        URI location = URI.create("/api/products/" + product.getId());
        return ResponseEntity.created(location).body(ProductResponse.from(product));
    }

    @GetMapping("/{productId}")
    public ProductResponse findById(@PathVariable("productId") Long productId) {
        Product product = productService.findById(productId);
        return ProductResponse.from(product);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll().stream().map(ProductResponse::from).toList();
    }

    @PutMapping("/{productId}")
    public ProductResponse update(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        Product product =
                productService.update(
                        productId, request.name(), request.price(), request.imageUrl());
        return ProductResponse.from(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable("productId") Long productId) {
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
