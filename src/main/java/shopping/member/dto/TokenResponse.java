package shopping.member.dto;

/**
 * 인증 토큰 응답 바디.
 * 예: {"token": "eyJhbGciOiJIUzI1NiJ9..."}
 *
 * @param token 발급한 인증 토큰(JWT)
 */
public record TokenResponse(String token) {
}
