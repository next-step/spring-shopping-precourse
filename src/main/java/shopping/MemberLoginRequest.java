package shopping;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record MemberLoginRequest(
        @NotBlank(message = "이메일은 필수입니다.")
                @Email(message = "이메일 형식이 올바르지 않습니다.")
                @Size(max = 254, message = "이메일은 최대 254자입니다.")
                String email,
        @NotBlank(message = "비밀번호는 필수입니다.")
                @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
                @ValidPasswordBytes
                String password) {
    public MemberLoginRequest {
        if (email != null) {
            email = email.strip().toLowerCase(Locale.ROOT);
        }
    }
}
