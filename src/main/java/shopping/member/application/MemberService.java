package shopping.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtTokenProvider;
import shopping.member.domain.Member;
import shopping.member.domain.MemberRepository;
import shopping.member.dto.MemberRequest;
import shopping.member.dto.TokenResponse;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.exception.LoginFailedException;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 생성자 주입: 스프링이 MemberRepository 구현체와 JwtTokenProvider를 넣어 준다.
    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 회원 가입: 이메일 중복을 확인한 뒤, 요청받은 이메일과 비밀번호로 회원을 만들어 저장하고,
     * 저장된 회원의 ID로 인증 토큰을 발급한다.
     * @return 발급한 인증 토큰
     */
    @Transactional
    public TokenResponse register(MemberRequest request) {
        validateDuplicateEmail(request.email());
        Member member = new Member(request.email(), request.password());
        Member savedMember = memberRepository.save(member);
        return new TokenResponse(jwtTokenProvider.createToken(savedMember.getId()));
    }

    /**
     * 로그인: 요청받은 이메일로 가입한 회원을 찾아, 그 회원의 ID로 인증 토큰을 발급한다.
     * 가입하지 않은 이메일이거나 비밀번호가 틀리면 LoginFailedException을 던진다.
     * @return 발급한 인증 토큰
     */
    @Transactional(readOnly = true)
    public TokenResponse login(MemberRequest request) {
        // 회원이 없으면(Optional이 비어 있으면) orElseThrow로 예외를 던진다.
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(LoginFailedException::new);
        validatePassword(member, request.password());
        return new TokenResponse(jwtTokenProvider.createToken(member.getId()));
    }

    // 입력한 비밀번호가 회원의 비밀번호와 다르면 예외를 던진다.
    private void validatePassword(Member member, String password) {
        if (!member.matchesPassword(password)) {
            throw new LoginFailedException();
        }
    }

    // 이미 같은 이메일로 가입한 회원이 있으면 예외를 던진다.
    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }
}
