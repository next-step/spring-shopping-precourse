package shopping.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 회원 가입 요청 바디.
 * 예: {"email": "user@test.com", "password": "password"}
 * 컨트롤러에서 @Valid로 검증하며, 규칙에 맞지 않으면 MethodArgumentNotValidException이 발생한다.
 * @param email    이메일 (비어 있거나 형식이 올바르지 않으면 예외)
 * @param password 비밀번호 (비어 있으면 예외)
 */
public record MemberRequest(
        // @NotBlank: null, 빈 문자열(""), 공백만 있는 문자열("  ")을 모두 막는다.
        // (@Email은 null과 빈 문자열을 통과시키기 때문에 따로 필요하다.)
        @NotBlank(message = "이메일은 비어 있을 수 없습니다.")
        // @Email 기본 규칙은 "user@localhost"처럼 도메인에 점(.)이 없어도 통과시키므로,
        // regexp로 "아이디@도메인.최상위도메인(2글자 이상)" 형태만 허용하도록 좁힌다.
        @Email(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "이메일 형식이 올바르지 않습니다."
        )
        String email,

        @NotBlank(message = "비밀번호는 비어 있을 수 없습니다.")
        String password
) {
}
