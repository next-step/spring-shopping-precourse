package shopping.member;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.TokenProvider;
import shopping.common.exception.ConflictException;
import shopping.common.exception.UnauthorizedException;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private static final String LOGIN_FAILED = "이메일 또는 비밀번호가 올바르지 않습니다.";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public MemberService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public TokenResponse register(MemberRequest request) {
        validateNotDuplicated(request.email());
        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = memberRepository.save(new Member(request.email(), encodedPassword));
        return createToken(member);
    }

    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException(LOGIN_FAILED));
        validatePassword(request.password(), member);
        return createToken(member);
    }

    private void validateNotDuplicated(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }
    }

    private void validatePassword(String rawPassword, Member member) {
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new UnauthorizedException(LOGIN_FAILED);
        }
    }

    private TokenResponse createToken(Member member) {
        return new TokenResponse(tokenProvider.createToken(member.getId()));
    }
}
