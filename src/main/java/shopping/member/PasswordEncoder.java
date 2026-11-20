package shopping.member;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {
    private static final String ALGORITHM = "SHA-256";

    public String encode(String rawPassword) {
        String password = Objects.requireNonNullElse(rawPassword, "");
        byte[] hash = createDigest().digest(password.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    private MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("비밀번호 암호화 알고리즘을 찾을 수 없습니다.", e);
        }
    }
}
