package shopping.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shopping.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email);
}
