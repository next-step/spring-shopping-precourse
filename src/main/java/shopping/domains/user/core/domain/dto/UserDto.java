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
@ToString
public class UserDto extends BaseDto {
    private final UUID id;

    private final String encryptedEmail;

    private final String encryptedPassword;

    @Builder(toBuilder = true)
    public UserDto(
            final UUID id,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final Long version,
            final String encryptedEmail,
            final String encryptedPassword
    ) {
        super(createdAt, updatedAt, deletedAt, version);
        this.id = id;
        this.encryptedEmail = encryptedEmail;
        this.encryptedPassword = encryptedPassword;
    }
}
