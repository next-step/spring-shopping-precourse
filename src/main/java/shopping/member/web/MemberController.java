package shopping.member.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shopping.auth.web.TokenResponse;
import shopping.member.application.MemberService;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService members;

    public MemberController(MemberService members) {
        this.members = members;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@RequestBody MemberRequest request) {
        String token = members.register(request.email(), request.password()).value();
        return new TokenResponse(token);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody MemberRequest request) {
        String token = members.login(request.email(), request.password()).value();
        return new TokenResponse(token);
    }
}
