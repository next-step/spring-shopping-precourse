package shopping.member.exception;

/**
 * 로그인에 실패했을 때 발생하는 예외.
 * 이메일이 없을 때와 비밀번호가 틀렸을 때 같은 메시지를 쓴다.
 * (어느 쪽이 틀렸는지 알려 주면 가입된 이메일을 추측할 수 있기 때문)
 */
public class LoginFailedException extends RuntimeException {

    public LoginFailedException() {
        super("이메일 또는 비밀번호가 일치하지 않습니다.");
    }
}
