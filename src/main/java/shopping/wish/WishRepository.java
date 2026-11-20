package shopping.wish;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Optional<Wish> findByIdAndMemberId(Long id, Long memberId);

    List<Wish> findAllByMemberId(Long memberId);
}
