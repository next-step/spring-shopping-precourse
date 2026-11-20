package shopping.product;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private ProductRequest request;
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request){
        this.request = request;
        Product product = productService.create(request.name(), request.price(), request.imageUrl());

        return ResponseEntity.created(URI.create("/api/products/" + product.getId()))
                .body(ProductResponse.from(product));
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long productId){
        Product product = productService.findById(productId);
        return  ResponseEntity.ok(ProductResponse.from(product));
    }
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll(){
        List<ProductResponse> responses = productService.findAll().stream()
                .map(ProductResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> update(@Valid @PathVariable Long productId,
                                                  @RequestBody ProductRequest request){
        Product product = productService.update(productId, request.name(),
                request.price(), request.imageUrl());
        return ResponseEntity.ok(ProductResponse.from(product));
    }
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId){
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}
