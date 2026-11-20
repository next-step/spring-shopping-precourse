package shopping;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;
    private final WishRepository wishRepository;

    public ProductService(
            ProductRepository productRepository,
            ProfanityChecker profanityChecker,
            WishRepository wishRepository) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
        this.wishRepository = wishRepository;
    }

    @Transactional
    public Product create(String name, Integer price, String imageUrl) {
        validateProfanity(name);
        Product product = new Product(name, price, imageUrl);
        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product findById(Long productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Product update(Long productId, String name, Integer price, String imageUrl) {
        Product product = findById(productId);
        validateProfanity(name);
        product.update(name, price, imageUrl);
        return product;
    }

    @Transactional
    public void delete(Long productId) {
        Product product =
                productRepository
                        .findByIdForUpdate(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));
        wishRepository.deleteAllByProduct_Id(productId);
        productRepository.delete(product);
    }

    private void validateProfanity(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new ProfanityDetectedException();
        }
    }
}
