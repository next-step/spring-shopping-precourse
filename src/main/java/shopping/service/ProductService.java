package shopping.service;

import java.util.List;

import org.springframework.stereotype.Service;

import shopping.client.PurgoMalumClient;
import shopping.domain.Product;
import shopping.exception.NotFoundException;
import shopping.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PurgoMalumClient purgoMalumClient;

    public ProductService(ProductRepository productRepository, PurgoMalumClient purgoMalumClient) {
        this.productRepository = productRepository;
        this.purgoMalumClient = purgoMalumClient;
    }

    public Product createProduct(String name, long price, String imageUrl) {
        Product product = new Product(null, name, price, imageUrl);

        if (purgoMalumClient.containsProfanity(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함되어 있습니다.");
        }

        return productRepository.save(product);
    }

    public Product findProduct(Long id) {
        Product product = productRepository.findById(id);

        if (product == null) {
            throw new NotFoundException("상품을 찾을 수 없습니다.");
        }

        return product;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Long id, String name, long price, String imageUrl) {
        Product product = productRepository.findById(id);

        if (product == null) {
            throw new NotFoundException("상품을 찾을 수 없습니다.");
        }

        Product newProduct = new Product(id, name, price, imageUrl);

        if (purgoMalumClient.containsProfanity(name)) {
            throw new IllegalArgumentException("상품 이름에 비속어가 포함되어 있습니다.");
        }

        return productRepository.update(id, newProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id);

        if (product == null) {
            throw new NotFoundException("상품을 찾을 수 없습니다.");
        }

        productRepository.deleteById(id);
    }
}
