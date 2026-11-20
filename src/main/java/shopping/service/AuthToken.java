package shopping.service;

public record AuthToken(String accessToken, long expiresIn) {
}
