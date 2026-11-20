package shopping.service;

public interface JwtProvider {

    String createToken(Long memberId);

    Long getMemberId(String token);

    long getExpiresInSeconds();
}
