package shopping.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BusinessException;
import shopping.common.exception.ErrorCode;
import shopping.product.client.PurgoMalumClient;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;
import shopping.product.entity.Product;
import shopping.product.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PurgoMalumClient purgoMalumClient;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        validateName(request.name());

        Product product = new Product(request.name(), request.price(), request.url());
        productRepository.save(product);

        return ProductResponse.from(product);
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

    @Transactional
    public void update(Long productId, ProductRequest request) {
        Product product = findProduct(productId);
        validateName(request.name());

        product.update(request.name(), request.price(), request.url());
    }

    @Transactional
    public void delete(Long productId) {
        Product product = findProduct(productId);

        productRepository.delete(product);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_PRODUCT));
    }

    private void validateName(String name) {
        if (purgoMalumClient.containsProfanity(name)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_PRODUCT_NAME);
        }
    }
}
