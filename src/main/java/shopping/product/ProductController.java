package shopping.product;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request){
        Product product = productService.create(request.name(), request.price(), request.imageUrl());

        return ResponseEntity.created(URI.create("/api/products/" + product.getId()))
                .body(ProductResponse.from(product));
    }
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long productId){
        Product product = productService.findById(productId);
        return  ResponseEntity.ok(ProductResponse.from(product));
    }

}
