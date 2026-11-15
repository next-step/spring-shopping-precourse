package shopping.product;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.NotFoundException;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.price(), request.imageUrl());
        validateNoProfanity(product.getName());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    public ProductResponse update(Long productId, ProductRequest request) {
        Product product = findProduct(productId);
        product.update(request.name(), request.price(), request.imageUrl());
        validateNoProfanity(product.getName());
        return ProductResponse.from(product);
    }

    public void delete(Long productId) {
        productRepository.delete(findProduct(productId));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
    }

    private void validateNoProfanity(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어를 포함할 수 없습니다.");
        }
    }
}
