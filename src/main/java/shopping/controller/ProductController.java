package shopping.controller;

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
import shopping.domain.Product;
import shopping.domain.ProductId;
import shopping.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        Product product = service.create(request.name(), request.priceValue(),
                request.imageUrl());
        ProductResponse response = ProductResponse.from(product);
        return ResponseEntity.created(URI.create("/api/products/" + response.id()))
                .body(response);
    }

    @GetMapping("/{productId}")
    public ProductResponse get(@PathVariable long productId) {
        return ProductResponse.from(service.get(new ProductId(productId)));
    }

    @GetMapping
    public List<ProductResponse> list() {
        return service.list().stream().map(ProductResponse::from).toList();
    }

    @PutMapping("/{productId}")
    public ProductResponse update(@PathVariable long productId,
            @RequestBody ProductRequest request) {
        Product product = service.update(new ProductId(productId), request.name(),
                request.priceValue(), request.imageUrl());
        return ProductResponse.from(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable long productId) {
        service.delete(new ProductId(productId));
        return ResponseEntity.noContent().build();
    }
}
