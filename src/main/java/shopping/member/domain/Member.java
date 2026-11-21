package shopping.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * 회원 엔티티. member 테이블의 한 행(row)과 연결된다.
 */
@Entity
public class Member {

    // 기본 키. DB가 AUTO_INCREMENT로 값을 만들어 준다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // JPA가 DB에서 조회한 데이터로 객체를 만들 때 쓰는 기본 생성자.
    // 외부에서는 쓰지 않도록 protected로 막아 둔다.
    protected Member() {
    }

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * 입력한 비밀번호가 이 회원의 비밀번호와 같은지 확인한다.
     * 비밀번호를 밖으로 꺼내 비교하지 않고, 회원 객체가 직접 판단하게 한다.
     */
    public boolean matchesPassword(String password) {
        return this.password.equals(password);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
