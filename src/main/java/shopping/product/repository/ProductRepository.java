package shopping.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.product.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);
}
