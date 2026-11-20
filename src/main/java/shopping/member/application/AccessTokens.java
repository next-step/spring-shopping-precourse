package shopping.member.application;

import java.util.Optional;

public interface AccessTokens {
    String issue(long memberId);

    Optional<Long> findMemberId(String token);
}
