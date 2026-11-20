package shopping.member.infrastructure;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import shopping.member.application.PasswordHasher;

@Component
public final class BCryptPasswordHasher implements PasswordHasher {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String dummyHash = encoder.encode("unused-account");

    @Override
    public String hash(String password) {
        return encoder.encode(password);
    }

    @Override
    public boolean matches(String password, Optional<String> storedHash) {
        return encoder.matches(password, storedHash.orElse(dummyHash)) && storedHash.isPresent();
    }
}
