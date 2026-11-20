package shopping.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtTokenProvider;
import shopping.common.DuplicateException;
import shopping.common.UnauthorizedException;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public TokenResponse register(MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateException("이미 가입된 이메일입니다.");
        }
        Member saved = memberRepository.save(new Member(request.email(), request.password()));
        return new TokenResponse(jwtTokenProvider.createToken(saved.getId()));
    }

    public TokenResponse login(MemberRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));
        checkPassword(member, request.password());
        return new TokenResponse(jwtTokenProvider.createToken(member.getId()));
    }

    private void checkPassword(Member member, String password) {
        if (!member.getPassword().equals(password)) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

}
