package shopping.domains.user.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class EncryptedPassword {
    private String value;

    @Builder(toBuilder = true)
    public EncryptedPassword(@NonNull final String value) {
        validate(value);

        this.value = value;
    }

    @Builder(
            builderClassName = "EncryptBuilder",
            builderMethodName = "encryptBuilder",
            toBuilder = true
    )
    public EncryptedPassword(
            @NonNull final RawPassword rawPassword,
            @NonNull final HashStrategy hashStrategy
    ) {
        this(hashStrategy.encrypt(rawPassword.getValue()));
    }

    private void validate(@NonNull final String value) {
        if(RawPassword.RAW_PASSWORD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("암호화되지 않은 비밀번호 입니다.");
        }
    }
}
