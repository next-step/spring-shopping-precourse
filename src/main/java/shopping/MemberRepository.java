package shopping;

import org.jspecify.annotations.Nullable;

public interface MemberRepository {
    @Nullable Member member(Email email);

    Member registeredMember(Email email, Password password);
}