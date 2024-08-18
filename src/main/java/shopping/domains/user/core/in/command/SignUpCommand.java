package shopping.domains.user.core.in.command;

import lombok.Builder;

public record SignUpCommand (
        String rawEmail,

        String rawPassword
) {
    @Builder(toBuilder = true)
    public SignUpCommand {

    }
}
