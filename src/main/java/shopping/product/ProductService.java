package shopping.product;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.NotFoundException;

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
        Price price = new Price(request.price());
        ImageUrl imageUrl = new ImageUrl(request.imageUrl());
        Product product = new Product(toCheckedName(request.name()), price, imageUrl);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        Price price = new Price(request.price());
        ImageUrl imageUrl = new ImageUrl(request.imageUrl());
        product.update(toCheckedName(request.name()), price, imageUrl);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(getProduct(id));
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 상품입니다."));
    }

    private ProductName toCheckedName(String value) {
        ProductName name = new ProductName(value);
        if (profanityChecker.containsProfanity(name.value())) {
            throw new IllegalArgumentException("상품 이름에 비속어를 사용할 수 없습니다.");
        }
        return name;
    }
}
