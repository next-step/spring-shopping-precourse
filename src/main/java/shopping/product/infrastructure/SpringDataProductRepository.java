package shopping.product.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.product.application.ProductRepository;
import shopping.product.domain.Product;

public interface SpringDataProductRepository
        extends JpaRepository<ProductJpaEntity, Long>, ProductRepository {
    @Override
    default long save(Product product) {
        ProductJpaEntity saved = save(new ProductJpaEntity(product));
        return saved.id();
    }
}
