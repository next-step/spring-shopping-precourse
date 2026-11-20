package shopping;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    boolean existsByMember_IdAndProduct_Id(Long memberId, Long productId);

    @EntityGraph(attributePaths = "product")
    List<Wish> findAllByMember_IdOrderByIdAsc(Long memberId);

    void deleteAllByProduct_Id(Long productId);
}
