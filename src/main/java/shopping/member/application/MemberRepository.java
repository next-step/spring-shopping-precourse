package shopping.member.application;

import java.util.Optional;
import shopping.member.domain.Member;

public interface MemberRepository {
    Optional<Long> save(String email, String passwordHash);

    Optional<Member> findByEmail(String email);
}
