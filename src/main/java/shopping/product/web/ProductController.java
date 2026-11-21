package shopping.product.web;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.product.application.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService products;

    public ProductController(ProductService products) {
        this.products = products;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody ProductRequest request) {
        return ProductResponse.of(
                products.create(request.name(), request.price(), request.imageUrl()));
    }

    @GetMapping
    public List<ProductResponse> list() {
        return products.list().stream().map(ProductResponse::of).toList();
    }

    @GetMapping("/{productId}")
    public ProductResponse find(@PathVariable Long productId) {
        return ProductResponse.of(products.find(productId));
    }

    @PutMapping("/{productId}")
    public ProductResponse update(@PathVariable Long productId,
            @RequestBody ProductRequest request) {
        return ProductResponse.of(
                products.update(productId, request.name(), request.price(), request.imageUrl()));
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long productId) {
        products.delete(productId);
    }
}
