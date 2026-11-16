package shopping;

import org.jspecify.annotations.NullMarked;

import java.util.regex.Pattern;

@NullMarked
public record Email(String localPart, Domain domain) {
    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*$");

    public Email {
        if (localPart.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (!LOCAL_PART_PATTERN.matcher(localPart).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 아이디입니다: " + localPart);
        }
    }

    public String fullAddress() {
        return localPart + "@" + domain.value();
    }
}
