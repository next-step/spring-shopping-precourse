package shopping.apps.shopping.api.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import shopping.apps.shopping.api.user.docs.UserApiDocs;
import shopping.domains.user.core.domain.entity.RawEmail;
import shopping.domains.user.core.domain.entity.RawPassword;
import shopping.domains.user.core.in.command.SignUpCommand;

public record RegisterRequest (
        @Schema(example = UserApiDocs.Email.EXAMPLE, description = UserApiDocs.Email.DESCRIPTION)
        @Pattern(regexp = RawEmail.REG_EXP, message = UserApiDocs.Password.REG_EXP_ERROR_MESSAGE)
        String email,

        @Schema(example = UserApiDocs.Password.EXAMPLE, description = UserApiDocs.Password.DESCRIPTION)
        @Pattern(regexp = RawPassword.RAW_PASSWORD_REG_EXP, message = UserApiDocs.Password.REG_EXP_ERROR_MESSAGE)
        String password
) {
    public SignUpCommand toCommand() {
        return SignUpCommand.builder()
                .rawEmail(email)
                .rawPassword(password)
                .build();
    }
}
