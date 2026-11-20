package shopping.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shopping.model.Wish;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Optional<Wish> findByIdAndMemberId(Long id, Long memberId);

    List<Wish> findAllByMemberId(Long memberId);
}
