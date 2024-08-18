package shopping.domains.common.core.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
public abstract class BaseDto {
    protected final LocalDateTime createdAt;

    protected final LocalDateTime updatedAt;

    protected final LocalDateTime deletedAt;

    protected final Long version;

    public BaseDto(
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final Long version
    ) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
