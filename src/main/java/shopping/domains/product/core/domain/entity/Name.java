package shopping.domains.product.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Name {
    public static final int MIN_NAME_LENGTH = 1;

    public static final int MAX_NAME_LENGTH = 15;

    public static final String REG_EXP = "^[\\p{L}\\p{N}\\s\\(\\)\\[\\]\\+\\-\\&\\/_]{"
            + MIN_NAME_LENGTH + ","
            + MAX_NAME_LENGTH + "}$";

    public static final Pattern PATTERN = Pattern.compile(REG_EXP);

    private String value;

    public Name(@NonNull final String value) {
        validate(value);

        this.value = value.trim();
    }

    private void validate(@NonNull final String value) {
        if (PATTERN.matcher(value.trim()).matches()) {
            return;
        }

        throw new IllegalArgumentException("올바른 상품 이름이 아닙니다.");
    }
}
