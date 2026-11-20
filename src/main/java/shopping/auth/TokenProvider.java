package shopping.auth;

public interface TokenProvider {

    String createToken(Long memberId);

    Long extractMemberId(String token);
}
