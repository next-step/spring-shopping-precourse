package shopping;

final class ProfanityCheckUnavailableException extends RuntimeException {
    ProfanityCheckUnavailableException() {
        super("상품 이름을 검사할 수 없습니다");
    }

    ProfanityCheckUnavailableException(Throwable cause) {
        super("상품 이름을 검사할 수 없습니다", cause);
    }
}
