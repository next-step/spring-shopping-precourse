package shopping;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@Profile("local")
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repository;

    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProductResponse> products() {
        return repository.products().stream().map(ProductResponse::response).toList();
    }

    @GetMapping("/{productId}")
    public ProductResponse product(@PathVariable("productId") Long id) {
        Product product = repository.product(id);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        return ProductResponse.response(product);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> registeredProduct(@RequestBody ProductRequest request) {
        long price = price(request);
        Product product = repository.registeredProduct(request.name(), price, request.url());
        return ResponseEntity.created(URI.create("/api/products/" + product.id()))
                .body(ProductResponse.response(product));
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable("productId") Long id, @RequestBody ProductRequest request) {
        repository.update(id, request.name(), price(request), request.url());
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable("productId") Long id) {
        repository.remove(id);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<Void> missingProduct() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Void> invalidProduct() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(ProfanityCheckUnavailableException.class)
    ResponseEntity<Void> unavailableProfanityCheck() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    private long price(ProductRequest request) {
        if (request.name() == null || request.price() == null
                || request.url() == null || request.url().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            return request.price().longValueExact();
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public record ProductRequest(String name, BigDecimal price, String url) {
    }
}
