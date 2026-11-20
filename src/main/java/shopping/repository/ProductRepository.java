package shopping.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import shopping.domain.Product;

@Repository
public class ProductRepository {

    private final Map<Long, Product> products = new HashMap<>();
    private long sequence = 1L;

    public Product save(Product product) {
        Product savedProduct = new Product(sequence, product.name(), product.price(), product.imageUrl());
        products.put(sequence, savedProduct);
        sequence++;

        return savedProduct;
    }

    public Product findById(Long id) {
        return products.get(id);
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public Product update(Long id, Product product) {
        Product updatedProduct = new Product(id, product.name(), product.price(), product.imageUrl());
        products.put(id, updatedProduct);

        return updatedProduct;
    }

    public void deleteById(Long id) {
        products.remove(id);
    }
}
