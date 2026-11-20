package shopping;

import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberAuthService memberAuthService;

    public MemberController(MemberAuthService memberAuthService) {
        this.memberAuthService = memberAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthTokenResponse> register(
            @Valid @RequestBody MemberRegisterRequest request) {
        AuthTokenResponse response =
                memberAuthService.register(request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        AuthTokenResponse response = memberAuthService.login(request.email(), request.password());
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
    }
}
