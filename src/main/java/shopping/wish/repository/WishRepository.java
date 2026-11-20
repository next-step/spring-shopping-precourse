package shopping.wish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shopping.wish.entity.Wish;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    List<Wish> findAllByMemberId(Long memberId);
}
