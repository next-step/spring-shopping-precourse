package shopping.product.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import shopping.product.domain.Product;

public interface ProductRepository {
    long save(Product product);

    List<StoredProduct> findProducts();

    Optional<StoredProduct> findProduct(long id);

    boolean existsProduct(long id);

    boolean updateProduct(long id, Product product);

    boolean deleteProduct(long id);

    record StoredProduct(long id, String name, BigDecimal price, String imageUrl) {
        static StoredProduct from(long id, Product product) {
            return new StoredProduct(id, product.name().name(), product.price(), product.imageUrl());
        }
    }
}
