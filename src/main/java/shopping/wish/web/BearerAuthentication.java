package shopping.wish.web;

import static shopping.shared.application.RequestFailure.Reason.AUTHENTICATION_REQUIRED;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import shopping.member.application.AccessTokens;
import shopping.shared.application.RequestFailure;

@Component
public final class BearerAuthentication {
    private static final Pattern HEADER = Pattern.compile(
            "Bearer +([A-Za-z0-9_-]{43})", Pattern.CASE_INSENSITIVE);

    private final AccessTokens tokens;

    public BearerAuthentication(AccessTokens tokens) {
        this.tokens = tokens;
    }

    public long requireMemberId(String authorization) {
        if (authorization == null) {
            throw authenticationRequired();
        }
        Matcher matcher = HEADER.matcher(authorization);
        if (!matcher.matches()) {
            throw authenticationRequired();
        }
        return tokens.findMemberId(matcher.group(1))
                .orElseThrow(BearerAuthentication::authenticationRequired);
    }

    private static RequestFailure authenticationRequired() {
        return new RequestFailure(AUTHENTICATION_REQUIRED, "유효한 인증 토큰이 필요합니다.");
    }
}
