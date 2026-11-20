package shopping.product.application;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import shopping.product.application.ProductRepository.StoredProduct;
import shopping.product.domain.Product;
import shopping.product.domain.ProductName;

@Service
public final class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    public StoredProduct register(String name, BigDecimal price, String imageUrl) {
        Product product = validateProduct(name, price, imageUrl);
        return StoredProduct.from(productRepository.save(product), product);
    }

    public List<StoredProduct> findAll() {
        return productRepository.findProducts();
    }

    public StoredProduct findById(long id) {
        return productRepository.findProduct(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public StoredProduct update(long id, String name, BigDecimal price, String imageUrl) {
        if (!productRepository.existsProduct(id)) {
            throw new ProductNotFoundException(id);
        }
        Product product = validateProduct(name, price, imageUrl);
        if (!productRepository.updateProduct(id, product)) {
            throw new ProductNotFoundException(id);
        }
        return StoredProduct.from(id, product);
    }

    public void delete(long id) {
        if (!productRepository.deleteProduct(id)) {
            throw new ProductNotFoundException(id);
        }
    }

    private Product validateProduct(String name, BigDecimal price, String imageUrl) {
        Product product = createProduct(name, price, imageUrl);
        if (profanityChecker.containsProfanity(product.name())) {
            throw new InvalidProductException("상품 이름에 비속어를 사용할 수 없습니다.");
        }
        return product;
    }

    private static Product createProduct(String name, BigDecimal price, String imageUrl) {
        try {
            return new Product(new ProductName(name), price, imageUrl);
        } catch (IllegalArgumentException exception) {
            throw new InvalidProductException(exception.getMessage(), exception);
        }
    }
}
