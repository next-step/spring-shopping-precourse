package shopping;

import java.sql.SQLException;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final String dummyPasswordHash;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.dummyPasswordHash = passwordEncoder.encode("unused-login-password");
    }

    @Transactional
    public Member register(String email, String password) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        validateDuplicateEmail(normalizedEmail);
        String passwordHash = passwordEncoder.encode(password);
        return saveMember(new Member(normalizedEmail, passwordHash));
    }

    @Transactional(readOnly = true)
    public Member login(String email, String password) {
        String normalizedEmail = email.strip().toLowerCase(Locale.ROOT);
        Member member =
                memberRepository
                        .findByEmail(normalizedEmail)
                        .orElseThrow(() -> missingMember(password));
        if (!passwordEncoder.matches(password, member.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return member;
    }

    private InvalidCredentialsException missingMember(String password) {
        passwordEncoder.matches(password, dummyPasswordHash);
        return new InvalidCredentialsException();
    }

    private Member saveMember(Member member) {
        try {
            return memberRepository.saveAndFlush(member);
        } catch (DataIntegrityViolationException exception) {
            if (exception.getMostSpecificCause() instanceof SQLException sql
                    && (sql.getErrorCode() == 1062 || "23505".equals(sql.getSQLState()))) {
                throw new DuplicateEmailException();
            }
            throw exception;
        }
    }

    private void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
    }
}
