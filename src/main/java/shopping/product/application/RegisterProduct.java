package shopping.product.application;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import shopping.product.domain.Product;
import shopping.product.domain.ProductName;

@Service
public final class RegisterProduct {
    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public RegisterProduct(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    public RegisteredProduct register(String name, BigDecimal price, String imageUrl) {
        Product product = createProduct(name, price, imageUrl);
        if (profanityChecker.containsProfanity(product.name())) {
            throw new InvalidProductException("상품 이름에 비속어를 사용할 수 없습니다.");
        }
        return new RegisteredProduct(productRepository.save(product), product);
    }

    private static Product createProduct(String name, BigDecimal price, String imageUrl) {
        try {
            return new Product(new ProductName(name), price, imageUrl);
        } catch (IllegalArgumentException exception) {
            throw new InvalidProductException(exception.getMessage(), exception);
        }
    }

    public record RegisteredProduct(long id, Product product) {}
}
