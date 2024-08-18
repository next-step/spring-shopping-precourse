package shopping.domains.user.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;
import shopping.domains.user.core.domain.dto.UserDto;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class User {
    private EncryptedEmail email;

    private EncryptedPassword password;

    @Builder(toBuilder = true)
    public User(
            @NonNull final EncryptedEmail email,
            @NonNull final EncryptedPassword password
    ) {
        this.email = email;
        this.password = password;
    }

    public User(@NonNull final UserDto dto) {
        this(
                new EncryptedEmail(dto.getEncryptedEmail()),
                new EncryptedPassword(dto.getEncryptedPassword())
        );
    }

    @NonNull
    public UserDto toDto() {
        return UserDto.builder()
                .encryptedEmail(email.getValue())
                .encryptedPassword(password.getValue())
                .build();
    }

    @NonNull
    public AuthToken signIn(
            @NonNull final RawPassword rawPassword,
            @NonNull final HashStrategy hashStrategy,
            @NonNull final AuthToken token
    ) {
        if (hashStrategy.match(rawPassword.getValue(), password.getValue())) {
            return token;
        }
        throw new IllegalArgumentException("올바르지 않은 이메일 또는 비밀번호 입니다.");
    }
}
