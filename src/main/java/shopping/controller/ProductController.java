package shopping.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.CreateProductRequest;
import shopping.domain.Product;
import shopping.PurgoMalumClient;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ProductController {
    private final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextProductId = new AtomicLong(1);
    private final PurgoMalumClient purgoMalumClient;

    @GetMapping("/api/products")
    public List<Product> getProducts() {
        return products.values().stream()
                .sorted(Comparator.comparing(Product::getProductId))
                .toList();
    }

    @PostMapping("/api/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProducts(@Valid @RequestBody CreateProductRequest request) {
        long productId = nextProductId.getAndIncrement();
        purgoMalumClient.validate(request.getProductName());
        Product product = new Product(request.getProductName(), productId, request.getPrice(), request.getImageUrl());
        products.put(productId, product);
        return product;
    }

    @GetMapping("/api/products/{productId}")
    public Product getProduct(@PathVariable("productId") Long productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw productNotFound();
        }
        return product;
    }

    @PutMapping("/api/products/{productId}")
    public Product updateProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody CreateProductRequest request) {
        getProduct(productId);
        purgoMalumClient.validate(request.getProductName());
        Product updated = new Product(
                request.getProductName(), productId, request.getPrice(), request.getImageUrl());
        if (products.replace(productId, updated) == null) {
            throw productNotFound();
        }
        return updated;
    }

    private ResponseStatusException productNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
    }

    @DeleteMapping("/api/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("productId") Long productId) {
        if (products.remove(productId) == null) {
            throw productNotFound();
        }
    }

    public ProductController(PurgoMalumClient purgoMalumClient) {
        this.purgoMalumClient = purgoMalumClient;
    }
}
