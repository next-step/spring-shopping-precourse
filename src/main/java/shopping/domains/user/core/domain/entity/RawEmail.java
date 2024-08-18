package shopping.domains.user.core.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
@ToString
public class RawEmail {
    public static final String REG_EXP = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    public static final Pattern PATTERN = Pattern.compile(REG_EXP);

    private String value;

    public RawEmail(@NonNull final String value) {
        validate(value);

        this.value = value;
    }

    private void validate(@NonNull final String email) {
        if(PATTERN.matcher(email).matches()) {
            return;
        }

        throw new IllegalArgumentException("올바르지 않은 이메일 형식입니다.");
    }
}
