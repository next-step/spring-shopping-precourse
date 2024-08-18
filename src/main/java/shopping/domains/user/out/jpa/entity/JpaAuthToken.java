package shopping.domains.user.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import shopping.domains.common.out.jpa.entity.BaseJpaEntity;
import shopping.domains.user.core.domain.dto.TokenDto;
import shopping.domains.user.core.domain.entity.AuthToken;

import java.util.UUID;

@Getter
@Entity
@Table(name = "user_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JpaAuthToken extends BaseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private UUID id;

    @Embedded
    @Getter(value = AccessLevel.NONE)
    private AuthToken authToken;

    public JpaAuthToken(@NonNull final TokenDto dto) {
        super(dto);

        this.authToken = dto.getDeletedAt() == null ? new AuthToken(dto) : null;
    }

    @NonNull
    public TokenDto toDto() {
        if (authToken == null) {
            return TokenDto.builder()
                    .userId(id)
                    .version(version)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .deletedAt(deletedAt)
                    .build();
        }
        return authToken.toDto()
                .toBuilder()
                .userId(id)
                .version(version)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
