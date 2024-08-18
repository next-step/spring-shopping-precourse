package shopping.domains.common.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import shopping.domains.common.core.domain.dto.BaseDto;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public abstract class BaseJpaEntity implements Persistable<UUID> {
    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;

    @Getter(value = AccessLevel.NONE)
    /**
     * soft delete 용 입니다.
     */
    protected LocalDateTime deletedAt;

    @Getter(value = AccessLevel.NONE)
    @Version
    @Column(nullable = false)
    protected Long version;

    public BaseJpaEntity(
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt,
            final LocalDateTime deletedAt,
            final Long version
    ) {
        validate(version);

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = Objects.nonNull(version) ? version : 0;
    }

    private void validate(final Long version) {
        if (version != null && version < 0) {
            throw new IllegalArgumentException("버전은 음수일 수 없습니다.");
        }
    }

    public BaseJpaEntity(@NonNull final BaseDto dto) {
        this(
                dto.getCreatedAt(),
                dto.getUpdatedAt(),
                dto.getDeletedAt(),
                dto.getVersion()
        );
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    @Override
    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    public abstract UUID getId();
}
