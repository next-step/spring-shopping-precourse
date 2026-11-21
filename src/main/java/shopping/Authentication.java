package shopping;

public final class Authentication {
    private final Registration registration;
    private final Login login;
    private final TokenRepository tokenRepository;

    public Authentication(Registration registration, Login login, TokenRepository tokenRepository) {
        this.registration = registration;
        this.login = login;
        this.tokenRepository = tokenRepository;
    }

    public String registrationToken(Email email, String rawPassword) {
        return tokenRepository.token(registration.member(email, rawPassword).id());
    }

    public String loginToken(Email email, String rawPassword) {
        return tokenRepository.token(login.member(email, rawPassword).id());
    }
}
