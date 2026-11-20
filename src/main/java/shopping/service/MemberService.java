package shopping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.entity.Member;
import shopping.entity.PasswordEncoder;
import shopping.exception.DuplicateEmailException;
import shopping.exception.InvalidCredentialsException;
import shopping.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthToken register(String email, String password) {
        Member member = Member.of(email, password, passwordEncoder);
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new DuplicateEmailException();
        }

        return issueToken(save(member));
    }

    @Transactional(readOnly = true)
    public AuthToken login(String email, String password) {
        Member member = memberRepository.findByEmail(Member.normalizeEmail(email))
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return issueToken(member);
    }

    private Member save(Member member) {
        try {
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(e);
        }
    }

    private AuthToken issueToken(Member member) {
        return new AuthToken(jwtProvider.createToken(member.getId()), jwtProvider.getExpiresInSeconds());
    }
}
