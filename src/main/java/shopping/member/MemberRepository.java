package shopping.member;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    private final Map<String, Member> members = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public Member save(Member member) {
        Member saved = member.withId(sequence.incrementAndGet());
        members.put(saved.getEmail(), saved);
        return saved;
    }

    public Optional<Member> findByEmail(String email) {
        return Optional.ofNullable(email).map(members::get);
    }
}