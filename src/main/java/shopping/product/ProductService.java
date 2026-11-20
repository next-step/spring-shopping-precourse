package shopping.product;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.exception.BadRequestException;
import shopping.common.exception.NotFoundException;
import shopping.product.dto.ProductRequest;
import shopping.product.dto.ProductResponse;

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
        Product product = request.toEntity();
        validateNoProfanity(product.getName());
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));        
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        ProductName name = new ProductName(request.name());
        Price price = new Price(request.price());
        ImageUrl imageUrl = new ImageUrl(request.imageUrl());
        validateNoProfanity(name.getValue());
        product.update(name, price, imageUrl);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(getProduct(id));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. id=" + id));
    }

    private void validateNoProfanity(String name) {
        if (profanityChecker.containsProfanity(name)) {
            throw new BadRequestException("상품 이름에 비속어를 사용할 수 없습니다.");
        }
    }
}
