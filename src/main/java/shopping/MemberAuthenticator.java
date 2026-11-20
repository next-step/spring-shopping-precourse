package shopping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class MemberAuthenticator {

    private static final Pattern BEARER =
            Pattern.compile("^Bearer +([A-Za-z0-9_-]+)$", Pattern.CASE_INSENSITIVE);
    private final TokenService tokenService;

    public MemberAuthenticator(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public Long authenticate(String authorization) {
        if (authorization == null) {
            throw new InvalidTokenException();
        }
        Matcher matcher = BEARER.matcher(authorization);
        if (!matcher.matches()) {
            throw new InvalidTokenException();
        }
        return tokenService.authenticate(matcher.group(1));
    }
}
