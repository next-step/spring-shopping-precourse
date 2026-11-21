package shopping.member.exception;

/**
 * 이미 가입된 이메일로 회원 가입을 시도했을 때 발생하는 예외.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("이미 가입된 이메일입니다.");
    }
}
