package shopping.domains.product.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopping.domains.product.out.jpa.entity.JpaProduct;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<JpaProduct, UUID> {
}
