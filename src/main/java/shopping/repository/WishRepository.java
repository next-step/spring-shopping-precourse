package shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopping.model.Member;
import shopping.model.Product;
import shopping.model.Wish;

import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    int deleteByMemberAndProductId(Member member, Long productId);

    List<Product> findProductsByMember(Member member);
}
