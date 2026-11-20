package shopping.wish;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shopping.member.Member;
import shopping.product.Product;

public interface WishRepository extends JpaRepository<Wish, Long> {

    List<Wish> findByMember(Member member);

    boolean existsByMemberAndProduct(Member member, Product product);
}
