package shopping.member.application;

import static shopping.shared.application.RequestFailure.Reason.CONFLICT;
import static shopping.shared.application.RequestFailure.Reason.INVALID_INPUT;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopping.shared.application.RequestFailure;

@Service
public class MemberService {
    private static final Pattern EMAIL = Pattern.compile("[^@\\s]+@[^@\\s.]+(?:\\.[^@\\s.]+)+");
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MAX_PASSWORD_BYTES = 72;

    private final MemberRepository members;
    private final PasswordHasher passwords;
    private final AccessTokens tokens;

    public MemberService(MemberRepository members, PasswordHasher passwords, AccessTokens tokens) {
        this.members = members;
        this.passwords = passwords;
        this.tokens = tokens;
    }

    @Transactional
    public String register(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (!validCredentials(normalizedEmail, password)) {
            throw new RequestFailure(INVALID_INPUT, "이메일 형식과 비밀번호를 확인해 주세요.");
        }
        long memberId = members.save(normalizedEmail, passwords.hash(password))
                .orElseThrow(() -> new RequestFailure(CONFLICT, "이미 가입된 이메일입니다."));
        return tokens.issue(memberId);
    }

    private static String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private static boolean validCredentials(String email, String password) {
        return email.length() <= MAX_EMAIL_LENGTH && EMAIL.matcher(email).matches()
                && password != null && !password.isBlank()
                && password.getBytes(StandardCharsets.UTF_8).length <= MAX_PASSWORD_BYTES;
    }
}
