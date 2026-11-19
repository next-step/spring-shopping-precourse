package shopping.product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.exception.NotFoundException;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }
    @Transactional
    public Product create(String name, int price, String imageUrl) {
        Product product = new Product(name, price, imageUrl);
        return productRepository.save(product);
    }
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품이 존재하지 않습니다."));
    }
}
