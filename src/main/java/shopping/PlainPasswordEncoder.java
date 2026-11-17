package shopping;

public class PlainPasswordEncoder implements PasswordEncoder {
    @Override
    public String hash(String rawPassword) {
        return rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String storedPassword) {
        return rawPassword.equals(storedPassword);
    }
}
