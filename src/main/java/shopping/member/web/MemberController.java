package shopping.member.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.member.application.MemberService;

@RestController
@RequestMapping(path = "/api/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {
    private final MemberService members;

    public MemberController(MemberService members) {
        this.members = members;
    }

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@RequestBody Credentials request) {
        return new TokenResponse(members.register(request.email(), request.password()));
    }

    public record Credentials(String email, String password) {}

    public record TokenResponse(String token) {}
}
