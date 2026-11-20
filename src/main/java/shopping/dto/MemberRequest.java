package shopping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import shopping.model.Email;

public record MemberRequest(
        @NotBlank @Pattern(regexp = Email.PATTERN) String email,
        @NotNull String password) {
}
