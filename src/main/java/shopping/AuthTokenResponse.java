package shopping;

public record AuthTokenResponse(String token, String tokenType, long expiresIn) {}
