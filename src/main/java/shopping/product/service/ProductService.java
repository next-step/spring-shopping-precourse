package shopping.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.entity.Product;
import shopping.product.profanity.ProfanityChecker;
import shopping.product.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProfanityChecker profanityChecker;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        validateName(request.name());
        if (productRepository.existsByName(request.name())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PRODUCT);
        }
        Product product = new Product(request.name(), request.price(), request.stock(), request.imgUrl());
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse getProduct(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = findProduct(productId);
        validateName(request.name());
        if (productRepository.existsByNameAndIdNot(request.name(), productId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PRODUCT);
        }
        product.updateProduct(request.name(), request.price(), request.stock(), request.imgUrl());
        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.delete(findProduct(productId));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void validateName(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_PROFANITY);
        }
    }
}
