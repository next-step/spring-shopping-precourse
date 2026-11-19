package shopping.product.application;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.domain.Product;
import shopping.product.domain.ProductName;
import shopping.product.domain.ProductPrice;
import shopping.product.domain.ProfanityChecker;
import shopping.product.domain.ProductRepository;
import shopping.product.dto.ProductCreateRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.dto.ProductUpdateRequest;

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
    public ProductResponse create(ProductCreateRequest request) {
        Product product = request.toEntity();
        validateNoProfanity(product.getName());
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse findById(Long productId) {
        return ProductResponse.from(getProduct(productId));
    }

    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAll();
        return ProductResponse.fromAll(products);
    }

    @Transactional
    public ProductResponse update(Long productId, ProductUpdateRequest request) {
        Product product = getProduct(productId);
        ProductName name = request.toProductName();
        ProductPrice price = request.toProductPrice();
        validateNoProfanity(name.getValue());
        product.update(name, price, request.imageUrl());
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long productId) {
        Product product = getProduct(productId);
        productRepository.delete(product);
    }

    private void validateNoProfanity(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어를 포함할 수 없습니다.");
        }
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. id=" + productId));
    }
}
