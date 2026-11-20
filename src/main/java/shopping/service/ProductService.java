package shopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.entity.Product;
import shopping.external.ProfanityChecker;
import shopping.repository.ProductRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public Product create(String name, int price, String imageUrl) {
        validateNoProfanity(name);
        Product product = Product.of(name, price, imageUrl);

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return getProduct(id);
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product update(Long id, String name, int price, String imageUrl) {
        Product product = getProduct(id);

        validateNoProfanity(name);
        product.update(name, price, imageUrl);

        return product;
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw productNotFound(id);
        }

        productRepository.deleteById(id);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> productNotFound(id));
    }

    private void validateNoProfanity(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new IllegalArgumentException("Product name contains profanity");
        }
    }

    private NoSuchElementException productNotFound(Long id) {
        return new NoSuchElementException("Product not found. id = " + id);
    }
}
