package shopping.domains.user.out.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import shopping.domains.common.out.jpa.entity.BaseJpaEntity;
import shopping.domains.user.core.domain.dto.UserDto;
import shopping.domains.user.core.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JpaUser extends BaseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private UUID id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email.value", column = @Column(name = "email")),
            @AttributeOverride(name = "password.value", column = @Column(name = "password"))
    })
    @Getter(value = AccessLevel.NONE)
    private User user;

    public JpaUser(@NonNull final UserDto dto) {
        super(dto);

        this.id = dto.getId();
        this.user = dto.getDeletedAt() == null ? new User(dto) : null;
    }

    @NonNull
    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    @NonNull
    public UserDto toDto() {
        if (user == null) {
            return UserDto.builder()
                    .id(id)
                    .version(version)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .deletedAt(deletedAt)
                    .build();
        }
        return user.toDto()
                .toBuilder()
                .id(id)
                .version(version)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }
}
