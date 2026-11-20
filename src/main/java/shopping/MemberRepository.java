package shopping;

import org.jspecify.annotations.Nullable;

public interface MemberRepository {
    @Nullable Member member(Email email);

    Member registered(Email email, Password password);
}