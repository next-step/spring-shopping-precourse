package shopping.dto;

import shopping.service.AuthToken;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

    private static final String BEARER = "Bearer";

    public static TokenResponse from(AuthToken authToken) {
        return new TokenResponse(authToken.accessToken(), BEARER, authToken.expiresIn());
    }
}
