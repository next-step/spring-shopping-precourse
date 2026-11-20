package shopping;

public class ProfanityServiceUnavailableException extends RuntimeException {

    public ProfanityServiceUnavailableException() {
        super("비속어 검사 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.");
    }

    public ProfanityServiceUnavailableException(Throwable cause) {
        super("비속어 검사 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해주세요.", cause);
    }
}
