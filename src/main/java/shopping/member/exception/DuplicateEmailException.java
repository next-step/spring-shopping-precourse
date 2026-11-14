package shopping.member.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(){
        super("이미 가입된 계정입니다.");
    }
}
