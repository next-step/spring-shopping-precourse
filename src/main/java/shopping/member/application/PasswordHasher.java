package shopping.member.application;

import java.util.Optional;

public interface PasswordHasher {
    String hash(String password);

    boolean matches(String password, Optional<String> storedHash);
}
