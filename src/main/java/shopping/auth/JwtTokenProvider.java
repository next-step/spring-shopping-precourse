package shopping.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shopping.auth.exception.InvalidTokenException;

/**
 * JWT 인증 토큰을 만들고 검증한다.
 * 토큰의 subject에 회원 ID를 담아, 이후 요청에서 어떤 회원인지 알아낼 수 있게 한다.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expireLength;

    // application.properties에 설정한 비밀 키와 만료 시간(ms)을 주입받는다.
    // 비밀 키 문자열은 HMAC-SHA 서명에 쓸 SecretKey 객체로 바꿔 둔다.
    public JwtTokenProvider(@Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.expire-length}") long expireLength) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expireLength = expireLength;
    }

    /**
     * 회원 ID를 담은 토큰을 만든다.
     * subject(회원 ID), 발급 시각, 만료 시각을 담고 비밀 키로 서명한다.
     */
    public String createToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireLength))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰이 유효한지 확인한다.
     * 우리 비밀 키로 서명한 토큰이고, 만료되지 않았으면 true를 돌려준다.
     */
    public boolean isValidToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException: 서명 불일치, 만료, 형식 오류 등
            // IllegalArgumentException: 토큰이 null이거나 빈 문자열인 경우
            return false;
        }
    }

    /**
     * 토큰에서 회원 ID를 꺼낸다.
     * createToken에서 subject에 넣어 둔 회원 ID(문자열)를 Long으로 바꿔 돌려준다.
     * 토큰이 없거나 유효하지 않으면 InvalidTokenException을 던진다.
     */
    public Long getMemberId(String token) {
        try {
            return Long.valueOf(parseClaims(token).getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            // 라이브러리 예외를 그대로 내보내지 않고, 우리가 정의한 예외로 바꿔 던진다.
            // (subject가 숫자가 아닐 때 나는 NumberFormatException도
            //  IllegalArgumentException의 하위 타입이라 함께 잡힌다.)
            throw new InvalidTokenException();
        }
    }

    // 비밀 키로 서명을 확인하고 토큰에 담긴 내용(Claims)을 꺼낸다.
    // 서명이 다르거나, 만료됐거나, 형식이 잘못되면 예외를 던진다.
    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
