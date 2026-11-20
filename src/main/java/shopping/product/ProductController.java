package shopping.product;

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
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        Product product = service.create(request.name(), request.price(), request.imageUrl());
        return ResponseEntity.created(URI.create("/api/products/" + product.getId()))
                .body(ProductResponse.from(product));
    }

    @GetMapping("/{productId}")
    public ProductResponse get(@PathVariable("productId") Long productId) {
        return ProductResponse.from(service.get(productId));
    }

    @GetMapping
    public List<ProductResponse> getAll() {
        return service.getAll().stream().map(ProductResponse::from).toList();
    }

    @PutMapping("/{productId}")
    public ProductResponse update(
            @PathVariable("productId") Long productId, @RequestBody ProductRequest request) {
        return ProductResponse.from(
                service.update(productId, request.name(), request.price(), request.imageUrl()));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable("productId") Long productId) {
        service.delete(productId);
        return ResponseEntity.noContent().build();
    }
}