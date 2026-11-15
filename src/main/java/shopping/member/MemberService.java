package shopping.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.common.DuplicateException;
import shopping.common.UnauthorizedException;

@Service
@Transactional
public class MemberService {
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse register(MemberRequest request) {
        validatePassword(request.password());
        validateNewEmail(request.email());
        Member member = new Member(request.email(), passwordEncoder.encode(request.password()));
        String token = member.issueToken();
        memberRepository.save(member);
        return new TokenResponse(token);
    }

    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!member.matchesPassword(passwordEncoder.encode(request.password()))) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return new TokenResponse(member.issueToken());
    }

    @Transactional(readOnly = true)
    public Member findByToken(String token) {
        return memberRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("유효하지 않은 토큰입니다."));
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
    }

    private void validateNewEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateException("이미 가입된 이메일입니다.");
        }
    }
}
