package shopping.domains.user.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class EncryptedEmail {
    private String value;

    @Builder(toBuilder = true)
    public EncryptedEmail(@NonNull final String value) {
        validate(value);

        this.value = value;
    }

    @Builder(
            builderClassName = "EncryptBuilder",
            builderMethodName = "encryptBuilder",
            toBuilder = true
    )
    public EncryptedEmail(
            @NonNull final RawEmail rawEmail,
            @NonNull final EncryptStrategy encryptStrategy
    ) {
        this(encryptStrategy.encrypt(rawEmail.getValue()));
    }

    private void validate(@NonNull final String email) {
        if(RawEmail.PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("암호화되지 않은 이메일 형식입니다.");
        }
    }
}
