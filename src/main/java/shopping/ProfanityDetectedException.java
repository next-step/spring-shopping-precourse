package shopping;

public class ProfanityDetectedException extends RuntimeException {

    public ProfanityDetectedException() {
        super("상품 이름에 비속어를 포함할 수 없습니다.");
    }
}
