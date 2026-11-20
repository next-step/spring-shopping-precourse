package shopping.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import shopping.domain.Product;
import shopping.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = productService.createProduct(request.name(), request.price(), request.imageUrl());

        return ResponseEntity.ok(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> findProduct(@PathVariable Long productId) {
        Product product = productService.findProduct(productId);

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAllProducts() {
        List<Product> products = productService.findAllProducts();

        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(productId, request.name(), request.price(), request.imageUrl());

        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.ok().build();
    }

    record ProductRequest(String name, long price, String imageUrl) {
    }
}
