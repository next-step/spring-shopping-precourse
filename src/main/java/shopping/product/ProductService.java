package shopping.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.NotFoundException;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    public ProductService(ProductRepository productRepository,
                          ProfanityChecker profanityChecker) {
        this.productRepository = productRepository;
        this.profanityChecker = profanityChecker;
    }

    public ProductResponse create(ProductRequest request) {
        checkProfanity(request.name());
        Product product = new Product(request.name(), request.price(), request.imageUrl());
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        checkProfanity(request.name());
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
        product.update(request.name(), request.price(), request.imageUrl());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
        productRepository.delete(product);
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from).toList();
    }

    private void checkProfanity(String name) {
        if (profanityChecker.contains(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함될 수 없습니다.");
        }
    }

}
