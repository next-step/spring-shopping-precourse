package shopping.domains.user.core.domain.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import shopping.domains.common.core.domain.dto.BaseDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TokenDto extends BaseDto {
    private final UUID userId;

    private final String accessToken;

    private final String refreshToken;

    @Builder(toBuilder = true)
    public TokenDto(
            final UUID userId,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final Long version,
            final String accessToken,
            final String refreshToken
    ) {
        super(createdAt, updatedAt, deletedAt, version);
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
