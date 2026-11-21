package shopping.wish.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByMember_IdAndProduct_Id(Long memberId, Long productId);

    List<Wish> findAllByMember_Id(Long memberId);
}
