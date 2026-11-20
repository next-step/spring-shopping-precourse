package shopping.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import shopping.domain.Member;

@Repository
public class MemberRepository {

    private final Map<Long, Member> members = new HashMap<>();
    private long sequence = 1L;

    public Member save(Member member) {
        Member savedMember = new Member(sequence, member.email(), member.password());
        members.put(sequence, savedMember);
        sequence++;

        return savedMember;
    }

    public Member findByEmail(String email) {
        return members.values().stream()
                .filter(member -> member.hasEmail(email))
                .findFirst()
                .orElse(null);
    }
}
