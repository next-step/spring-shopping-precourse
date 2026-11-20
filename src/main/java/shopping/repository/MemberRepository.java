package shopping.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import shopping.model.Email;
import shopping.model.Member;

@Repository
public class MemberRepository {
    private final AtomicLong sequence = new AtomicLong();
    private final Map<Long, Member> members = new ConcurrentHashMap<>();
    private final Map<Email, Member> membersByEmail = new ConcurrentHashMap<>();

    public synchronized Member save(Member member) {
        member.assignId(sequence.incrementAndGet());
        members.put(member.getId(), member);
        membersByEmail.put(member.getEmail(), member);
        return member;
    }

    public boolean existsByEmail(Email email) {
        return membersByEmail.containsKey(email);
    }

    public Optional<Member> findById(Long memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    public Optional<Member> findByEmail(Email email) {
        return Optional.ofNullable(membersByEmail.get(email));
    }
}
