package shopping.product;

import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityValidator profanityValidator;

    public ProductService(
            ProductRepository productRepository, ProfanityValidator profanityValidator) {
        this.productRepository = productRepository;
        this.profanityValidator = profanityValidator;
    }

    public ProductResponse create(ProductRequest request) {
        profanityValidator.validate(request.name());
        Product product = new Product(request.name(), request.price(), request.imageUrl());
        return ProductResponse.from(productRepository.save(product));
    }
}
