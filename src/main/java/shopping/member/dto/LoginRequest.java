package shopping.member.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "EMAIL_MISSING")
        String email,

        @NotBlank(message = "PASSWORD_MISSING")
        String password
) {
}
