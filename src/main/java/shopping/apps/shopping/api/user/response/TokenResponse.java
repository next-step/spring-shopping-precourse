package shopping.apps.shopping.api.user.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import shopping.apps.shopping.api.user.docs.UserApiDocs;
import shopping.domains.user.core.domain.dto.TokenDto;

public record TokenResponse(
        @Schema(example = UserApiDocs.Token.ACCESS_TOKEN_EXAMPLE, description = UserApiDocs.Token.ACCESS_TOKEN_DESCRIPTION)
        String accessToken,

        @Schema(example = UserApiDocs.Token.REFRESH_TOKEN_EXAMPLE, description = UserApiDocs.Token.REFRESH_TOKEN_DESCRIPTION)
        String refreshToken
) {
    public TokenResponse(@NonNull final TokenDto tokenDto) {
        this(
                tokenDto.getAccessToken(),
                tokenDto.getRefreshToken()
        );
    }
}
