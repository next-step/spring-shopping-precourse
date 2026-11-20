package shopping.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.UnauthorizedException;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private static final String LOGIN_FAILED_MESSAGE = "이메일 또는 비밀번호가 올바르지 않습니다.";

    private final MemberRepository memberRepository;
    private final PasswordHasher passwordHasher;

    public MemberService(MemberRepository memberRepository, PasswordHasher passwordHasher) {
        this.memberRepository = memberRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public TokenResponse register(MemberRequest request) {
        validatePassword(request.password());
        Member member = new Member(request.email(), passwordHasher.hash(request.password()));
        validateNotRegistered(request.email());
        String token = member.issueToken();
        memberRepository.save(member);
        return new TokenResponse(token);
    }

    @Transactional
    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(LOGIN_FAILED_MESSAGE));
        validatePasswordMatches(member, request.password());
        return new TokenResponse(member.issueToken());
    }

    public Member findByToken(String token) {
        return memberRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 토큰입니다."));
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
    }

    private void validateNotRegistered(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }

    private void validatePasswordMatches(Member member, String password) {
        if (password == null || !member.hasPassword(passwordHasher.hash(password))) {
            throw new UnauthorizedException(LOGIN_FAILED_MESSAGE);
        }
    }
}
