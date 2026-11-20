package shopping.product;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.NotFoundException;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository, ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.price(), request.imageUrl());
        validateProfanity(request.name());
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse findById(Long productId) {
        return ProductResponse.from(getProduct(productId));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long productId, ProductRequest request) {
        Product product = getProduct(productId);
        product.update(request.name(), request.price(), request.imageUrl());
        validateProfanity(request.name());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long productId) {
        productRepository.delete(getProduct(productId));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
    }

    private void validateProfanity(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어를 포함할 수 없습니다.");
        }
    }
}
