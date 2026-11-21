package shopping.auth.web;

import org.springframework.stereotype.Component;
import shopping.auth.application.AuthenticationService;

@Component
public class CurrentMemberResolver {

    private final AuthenticationService authentication;

    public CurrentMemberResolver(AuthenticationService authentication) {
        this.authentication = authentication;
    }

    public Long resolve(String authorization) {
        return authentication.authenticate(BearerToken.parse(authorization));
    }
}
