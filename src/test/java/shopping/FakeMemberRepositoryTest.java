package shopping;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

class FakeMemberRepositoryTest {

    private Email tomEmail;
    private Password tomPassword;
    private Member tom;
    private MemberRepository memberRepository;


    @BeforeEach
    void setUp() {
        // given
        tomEmail = new Email("Tom", new Domain("gmail.com"));
        tomPassword = new Password("0123456789");
        tom = new Member(1L, tomEmail, tomPassword);
        Email atorEmail = new Email("Ator", new Domain("gmail.com"));
        Member ator = new Member(2L, atorEmail, new Password("0123456789"));

        memberRepository = new FakeMemberRepository(
                Map.of(
                        tomEmail, tom,
                        atorEmail, ator
                ),
                3L
        );
    }

    @Test
    @DisplayName("이미 존재하는 회원을 이메일로 찾")
    void test1() {
        // when
        var memberTom = memberRepository.member(tomEmail);

        // then
        Assertions.assertThat(memberTom).isEqualTo(tom);
    }

    @Test
    @DisplayName("이미 회원이 사용하는 이메일이 있다면, 그것으로 가입 불가")
    void test2() {
        // when
        var email = tomEmail;
        var password = tomPassword;

        Assertions.assertThatThrownBy(() -> memberRepository.registered(email, password)).isInstanceOf(IllegalArgumentException.class);

    }
}