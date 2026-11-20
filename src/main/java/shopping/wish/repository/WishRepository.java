package shopping.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.wish.domain.Wish;
import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
