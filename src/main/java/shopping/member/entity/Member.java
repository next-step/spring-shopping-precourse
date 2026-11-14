package shopping.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;//BCrypto로 암호화 예정

    public Member(String name, String email, String password){//BCrypto로 암호화 예정
        validateNotBlank(email, "이메일은 필수입니다.");
        validateNotBlank(password, "비밀번호는 필수입니다.");
        this.email = email;
        this.password = password;//BCrypto로 암호화 예정
    }

    private void validateNotBlank(String value, String message){
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException(message);
        }
    }
}