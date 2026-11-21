package shopping.wish.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shopping.wish.domain.Wish;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByMemberIdAndProduct_Id(Long memberId, Long productId);

    @Query("select w from Wish w join fetch w.product where w.memberId = :memberId order by w.id")
    List<Wish> findAllWithProducts(@Param("memberId") Long memberId);
}
