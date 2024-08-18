package shopping.domains.user.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import shopping.domains.user.core.domain.dto.TokenDto;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class AuthToken {
    private String accessToken;

    private String refreshToken;

    @Builder(toBuilder = true)
    public AuthToken(
            @NonNull final String accessToken,
            @NonNull final String refreshToken
    ) {
        validate(accessToken, refreshToken);

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthToken(@NonNull final TokenDto dto) {
        this(
                dto.getAccessToken(),
                dto.getRefreshToken()
        );
    }

    @NonNull
    public TokenDto toDto() {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void validate(
            @NonNull final String accessToken,
            @NonNull final String refreshToken
    ) {
        if (accessToken.equals(refreshToken)) {
            throw new IllegalArgumentException("토큰값은 같을 수 없습니다.");
        }
    }
}
