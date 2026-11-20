package shopping.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityValidator profanityValidator;

    public ProductService(
            ProductRepository productRepository, ProfanityValidator profanityValidator) {
        this.productRepository = productRepository;
        this.profanityValidator = profanityValidator;
    }

    public ProductResponse findById(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"));
    }

    public ProductResponse create(ProductRequest request) {
        profanityValidator.validate(request.name());
        Product product = new Product(request.name(), request.price(), request.imageUrl());
        return ProductResponse.from(productRepository.save(product));
    }
}
