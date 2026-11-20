package shopping.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberSignUpRequestDto(
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자 이상 16자 미만이어야 합니다.")
        String password
) { }
