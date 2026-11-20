package shopping.product.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import shopping.product.application.ProductRepository.StoredProduct;
import shopping.product.domain.Product;

@Entity
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String price;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    protected ProductJpaEntity() {}

    ProductJpaEntity(Product product) {
        name = product.name().name();
        price = product.price().toString();
        imageUrl = product.imageUrl();
    }

    long id() {
        return id;
    }

    StoredProduct toStoredProduct() {
        return new StoredProduct(id, name, new BigDecimal(price), imageUrl);
    }
}
