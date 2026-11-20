package shopping.wish;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    @EntityGraph(attributePaths = "product")
    List<Wish> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
