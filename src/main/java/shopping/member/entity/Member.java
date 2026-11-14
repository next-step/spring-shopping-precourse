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

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;//BCrypto로 암호화 예정

    public Member(String name, String email, String password){//BCrypto로 암호화 예정
        this.name = name;
        this.email = email;
        this.password = password;//BCrypto로 암호화 예정
    }

}