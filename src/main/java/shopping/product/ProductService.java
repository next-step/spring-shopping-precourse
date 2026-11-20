package shopping.product;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final ProductNameFactory nameFactory;

    public ProductService(ProductRepository repository, ProductNameFactory nameFactory) {
        this.repository = repository;
        this.nameFactory = nameFactory;
    }

    public Product create(String name, long price, String imageUrl) {
        Product product = new Product(null, nameFactory.create(name), price, imageUrl);
        return repository.save(product);
    }

    public Product get(Long id) {
        return findOrThrow(id);
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product update(Long id, String name, long price, String imageUrl) {
        Product found = findOrThrow(id);
        return repository.save(found.update(nameFactory.create(name), price, imageUrl));
    }

    public void delete(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
    }

    private Product findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}