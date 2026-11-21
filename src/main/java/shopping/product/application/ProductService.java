package shopping.product.application;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.Product;
import shopping.product.domain.ProfanityChecker;
import shopping.product.persistence.ProductRepository;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository products;
    private final ProfanityChecker checker;

    public ProductService(ProductRepository products, ProfanityChecker checker) {
        this.products = products;
        this.checker = checker;
    }

    @Transactional
    public Product create(String name, Integer price, String imageUrl) {
        Product product = new Product(name, price, imageUrl);
        product.checkProfanity(checker);

        return products.save(product);
    }

    public List<Product> list() {
        return products.findAll(Sort.by("id"));
    }

    public Product find(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new ShoppingException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional
    public Product update(Long id, String name, Integer price, String imageUrl) {
        Product product = find(id);
        product.changeTo(name, price, imageUrl, checker);

        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = find(id);
        products.delete(product);
    }
}
