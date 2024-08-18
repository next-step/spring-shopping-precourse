package shopping.domains.user.core.domain.entity;

import lombok.Getter;
import lombok.NonNull;

import java.util.regex.Pattern;

@Getter
public class RawPassword {
    public static final int MIN_RAW_PASSWORD_LENGTH = 8;

    public static final int MAX_RAW_PASSWORD_LENGTH = 20;

    public static final String RAW_PASSWORD_REG_EXP = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{"
            + MIN_RAW_PASSWORD_LENGTH + ","
            + MAX_RAW_PASSWORD_LENGTH + "}$";

    public static final Pattern RAW_PASSWORD_PATTERN = Pattern.compile(RAW_PASSWORD_REG_EXP);

    private String value;

    public RawPassword(@NonNull final String value) {
        validate(value);

        this.value = value;
    }

    private void validate(@NonNull final String value) {
        if(!RAW_PASSWORD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("형식에 맞지 않는 비밀번호 입니다.");
        }
    }
}
