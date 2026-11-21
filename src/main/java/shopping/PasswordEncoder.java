package shopping;

public interface PasswordEncoder {

    String hash(String rawPassword);

    boolean matches(
            String rawPassword,
            String storedPassword
    );
}