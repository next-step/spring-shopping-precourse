package shopping.member.service;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.member.dto.LoginRequestDto;
import shopping.member.dto.LoginResponseDto;
import shopping.member.dto.MemberSignUpRequestDto;
import shopping.member.dto.MemberSignupResponseDto;
import shopping.member.entity.Member;
import shopping.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberSignupResponseDto signup(MemberSignUpRequestDto request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        String uuid = UUID.randomUUID().toString();
        memberRepository.save(new Member(request.email(), request.password(), uuid));
        return new MemberSignupResponseDto(uuid);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
        if (!member.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return new LoginResponseDto(member.getUuid());
    }
}
