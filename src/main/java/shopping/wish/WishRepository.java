package shopping.wish;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    List<Wish> findAllByMemberId(Long memberId);
}