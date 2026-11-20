package shopping;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class FakeMemberRepository implements MemberRepository {
    private final HashMap<Email, Member> members;
    private long nextId;

    public FakeMemberRepository(Map<Email, Member> members, long nextId) {
        this.members = new HashMap<>(members);
        this.nextId = nextId;
    }


    @Override
    public synchronized @Nullable Member member(Email email) {
        return members.get(email);
    }

    @Override
    public synchronized Member registeredMember(Email email, Password password) {
        if (member(email) != null) {
            throw new IllegalArgumentException("이미 등록된 회원");
        }
        var newMember = new Member(nextId++, email, password);
        members.put(email, newMember);
        return newMember;
    }
}