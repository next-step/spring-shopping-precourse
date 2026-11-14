package shopping.member.service;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.auth.JwtProvider;
import shopping.member.dto.*;
import shopping.member.entity.Member;
import shopping.member.exception.AuthenticationFailedException;
import shopping.member.exception.DuplicateEmailException;
import shopping.member.repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;


    @Transactional
    public TokenResponse register(MemberRegisterRequest request){
        if (memberRepository.existsByEmail(request.email())){
            throw new DuplicateEmailException();
        }
        Member member = memberRepository.save(new Member(request.email(), passwordEncoder.encode(request.password())));
        return new TokenResponse(jwtProvider.createToken(member.getId(), member.getEmail()));
    }

    @Transactional(readOnly = true)
    public TokenResponse login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(AuthenticationFailedException::new);

        if (!passwordEncoder.matches(request.password(), member.getPassword())){
            throw new AuthenticationFailedException();
        }
        return new TokenResponse(jwtProvider.createToken(member.getId(), member.getEmail()));
    }
}
