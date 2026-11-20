package shopping.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.dto.ProductRequest;
import shopping.dto.ProductResponse;
import shopping.entity.Product;
import shopping.service.ProductService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest request) {
        Product product = productService.create(request.name(), request.price(), request.imageUrl());

        return ResponseEntity.created(URI.create("/api/products/" + product.getId()))
                .body(ProductResponse.from(product));
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @GetMapping("/{productId}")
    public ProductResponse findById(@PathVariable Long productId) {
        return ProductResponse.from(productService.findById(productId));
    }

    @PutMapping("/{productId}")
    public ProductResponse update(@PathVariable Long productId, @RequestBody @Valid ProductRequest request) {
        Product product = productService.update(productId, request.name(), request.price(), request.imageUrl());

        return ProductResponse.from(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productService.delete(productId);

        return ResponseEntity.noContent().build();
    }
}
