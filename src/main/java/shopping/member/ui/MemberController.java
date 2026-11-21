package shopping.member.ui;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopping.member.application.MemberService;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;

/**
 * 회원 API.
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * POST /api/members/register : 회원 가입
     * 가입에 성공하면 201 Created와 함께 인증 토큰을 응답 바디에 담아 준다.
     * @Valid: MemberRequest에 붙은 검증 규칙(@Email 등)을 서비스 호출 전에 검사한다.
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody MemberRequest request) {
        TokenResponse response = memberService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/members/login : 로그인
     * 이메일과 비밀번호를 받아 로그인하고, 200 OK와 함께 인증 토큰을 응답 바디에 담아 준다.
     * @Valid: 회원 가입과 같은 MemberRequest 검증 규칙(빈 값, 이메일 형식)을 적용한다.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.login(request));
    }
}
