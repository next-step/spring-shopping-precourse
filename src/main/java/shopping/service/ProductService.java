package shopping.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import shopping.dto.ProductRequest;
import shopping.dto.ProductResponse;
import shopping.model.Product;
import shopping.model.ProductName;
import shopping.repository.ProductRepository;
import shopping.validation.ProfanityValidator;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProfanityValidator profanityValidator;

    public ProductService(
            ProductRepository productRepository, ProfanityValidator profanityValidator) {
        this.productRepository = productRepository;
        this.profanityValidator = profanityValidator;
    }

    public void delete(Long productId) {
        productRepository.delete(findProduct(productId));
    }

    public ProductResponse update(Long productId, ProductRequest request) {
        Product product = findProduct(productId);
        profanityValidator.validate(request.name());
        product.update(new ProductName(request.name()), request.price(), request.imageUrl());
        return ProductResponse.from(product);
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse findById(Long productId) {
        return ProductResponse.from(findProduct(productId));
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"));
    }

    public ProductResponse create(ProductRequest request) {
        profanityValidator.validate(request.name());
        Product product = new Product(
                new ProductName(request.name()), request.price(), request.imageUrl());
        return ProductResponse.from(productRepository.save(product));
    }
}
