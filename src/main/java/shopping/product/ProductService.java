package shopping.product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.exception.BadRequestException;
import shopping.exception.NotFoundException;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final PurgoMalumClient purgoMalumClient;

    public ProductService(ProductRepository productRepository, PurgoMalumClient purgoMalumClient){
        this.productRepository = productRepository;
        this.purgoMalumClient = purgoMalumClient;
    }
    @Transactional
    public Product create(String name, int price, String imageUrl) {
        validateProfanity(name);
        Product product = new Product(name, price, imageUrl);
        return productRepository.save(product);
    }
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품이 존재하지 않습니다."));
    }
    @Transactional(readOnly = true)
    public List<Product> findAll(){
        return  productRepository.findAll();
        }
    @Transactional
    public Product update(Long id, String name, int price, String imageUrl){
        validateProfanity(name);
        Product product = findById(id);
        product.update(name, price, imageUrl);
        return product;
    }
    @Transactional
    public void delete(Long id){
        Product product = findById(id);
        productRepository.delete(product);
    }
    private void validateProfanity(String name) {
        if (purgoMalumClient.containsProfanity(name)) {
            throw new BadRequestException("상품 이름에 비속어를 포함할 수 없습니다.");
        }
    }

}

