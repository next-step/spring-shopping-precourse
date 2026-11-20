package shopping.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "EMAIL_MISSING")
        @Email(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "INVALID_EMAIL_FORMAT")
        String email,

        @NotBlank(message = "PASSWORD_MISSING")
        @Size(min = 15, max = 72, message = "INVALID_PASSWORD_LENGTH")
        @Pattern(regexp = "^[\\x21-\\x7E]*$", message = "INVALID_PASSWORD_CHARACTER")
        String password,

        @NotBlank(message = "NAME_MISSING")
        @Size(max = 39, message = "NAME_TOO_LONG")
        String name
) {
}
