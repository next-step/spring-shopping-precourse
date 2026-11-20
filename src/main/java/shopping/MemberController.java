package shopping;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Profile("local")
@RequestMapping("/api/members")
public class MemberController {
    private final Authentication authentication;

    public MemberController(Authentication authentication) {
        this.authentication = authentication;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registrationToken(@RequestBody RegistrationRequest request) {
        try {
            if (request.email() == null || request.password() == null) {
                throw new IllegalArgumentException();
            }
            String token = authentication.registrationToken(email(request.email()), request.password());
            return new TokenResponse(token);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private Email email(String address) {
        String[] parts = address.split("@", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException();
        }
        return new Email(parts[0], new Domain(parts[1]));
    }

    public record RegistrationRequest(String email, String password) {
    }

    public record TokenResponse(String token) {
    }
}
