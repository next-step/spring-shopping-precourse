package shopping.product.application;

import shopping.product.domain.Product;

public interface ProductRepository {
    long save(Product product);
}
