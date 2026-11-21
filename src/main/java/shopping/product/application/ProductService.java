package shopping.product.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;
import shopping.product.domain.ProductRepository;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.exception.ProfanityProductNameException;
import shopping.product.exception.ProductNotFoundException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        validateProductName(request.name());
        Product product = new Product(request.name(), request.price(), request.imageUrl());
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long productId, ProductRequest request) {
        Product product = findProduct(productId);
        validateProductName(request.name());
        product.update(request.name(), request.price(), request.imageUrl());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long productId) {
        Product product = findProduct(productId);
        productRepository.delete(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
    }

    private void validateProductName(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new ProfanityProductNameException();
        }
    }
}
