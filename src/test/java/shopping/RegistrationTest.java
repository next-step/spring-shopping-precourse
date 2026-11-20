package shopping;

import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationTest {
    private final Email email = new Email("tom", new Domain("gmail.com"));
    private MemberRepository repository;
    private Registration registration;

    @BeforeEach
    void setUp() {
        repository = new FakeMemberRepository(Map.of(), 1L);
        registration = new Registration(repository, new PlainPasswordEncoder());
    }

    @Test
    void 가입한_회원에게_ID를_부여하고_저장한다() {
        var member = registration.member(email, "0123456789");

        Assertions.assertThat(member.id()).isEqualTo(1L);
        Assertions.assertThat(member.email()).isEqualTo(email);
        Assertions.assertThat(repository.member(email)).isEqualTo(member);
    }

    @Test
    void 중복_가입은_거부하고_기존_회원을_유지한다() {
        var original = registration.member(email, "0123456789");

        Assertions.assertThatThrownBy(() -> registration.member(email, "abcdefghij"))
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertThat(repository.member(email)).isEqualTo(original);
    }

    // 원문이 9자면 저장하지 않는지, 인코딩된 값이 저장되는지,
    // 인코딩 결과가 길어도 짧은 원문을 거부하는지 테스트할 수 있다. 생략.
}
