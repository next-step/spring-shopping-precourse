package shopping.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.exception.ConflictException;
import shopping.exception.UnauthorizedException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberService(MemberRepository memberRepository, JwtProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public String register(String email, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }
        Member member = memberRepository.save(new Member(email, password));
        return jwtProvider.createToken(member.getId());
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!member.matchesPassword(password)) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return jwtProvider.createToken(member.getId());
    }
}