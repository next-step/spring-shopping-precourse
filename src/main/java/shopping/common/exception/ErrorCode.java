package shopping.common.exception;


import com.sun.net.httpserver.HttpsServer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //멤버
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 계정입니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    //상품


    //위시리스트

    //공통
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.")
    ;

    private final HttpStatus status;
    private final String message;
}
