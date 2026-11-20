package shopping.product.infrastructure;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import shopping.product.application.ProductRepository;
import shopping.product.domain.Product;

public interface SpringDataProductRepository
        extends JpaRepository<ProductJpaEntity, Long>, ProductRepository {
    @Override
    default long save(Product product) {
        ProductJpaEntity saved = save(new ProductJpaEntity(product));
        return saved.id();
    }

    @Override
    default List<StoredProduct> findProducts() {
        return findAll(Sort.by("id")).stream()
                .map(ProductJpaEntity::toStoredProduct)
                .toList();
    }

    @Override
    default Optional<StoredProduct> findProduct(long id) {
        return findById(id).map(ProductJpaEntity::toStoredProduct);
    }

    @Override
    default boolean existsProduct(long id) {
        return existsById(id);
    }

    @Override
    default boolean updateProduct(long id, Product product) {
        return updateRow(id, product.name().name(), product.price().toString(), product.imageUrl()) > 0;
    }

    @Override
    default boolean deleteProduct(long id) {
        return deleteRow(id) > 0;
    }

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("""
            update ProductJpaEntity p
            set p.name = :name, p.price = :price, p.imageUrl = :imageUrl
            where p.id = :id
            """)
    int updateRow(@Param("id") long id, @Param("name") String name,
            @Param("price") String price, @Param("imageUrl") String imageUrl);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    @Query("delete from ProductJpaEntity p where p.id = :id")
    int deleteRow(@Param("id") long id);
}
