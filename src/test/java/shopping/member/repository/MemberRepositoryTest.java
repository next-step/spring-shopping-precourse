package shopping.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import shopping.member.domain.Member;
import java.util.Optional;

@DataJpaTest
public class MemberRepositoryTest {
    private static final String EMAIL = "a@a.com";

    @Autowired
    private  MemberRepository memberRepository;

    @Test
    void 이메일로_회원을_찾을_수_있다() {
        memberRepository.save(new Member(null, EMAIL, "hashed"));

        Optional<Member> found = memberRepository.findByEmail(EMAIL);

        assertThat(found).isPresent();
        assertThat(found.get().getPassword()).isEqualTo("hashed");
    }

    @Test
    void 없는_이메일이면_빈_결과를_돌려준다() {
        assertThat(memberRepository.findByEmail("none@a.com")).isEmpty();
    }

    @Test
    void 가입된_이메일은_존재한다고_답한다() {
        memberRepository.save(new Member(null, EMAIL, "hashed"));

        assertThat(memberRepository.existsByEmail(EMAIL)).isTrue();
    }

    @Test
    void 가입되지_않은_이메일은_존재하지_않는다고_답한다() {
        assertThat(memberRepository.existsByEmail("none@a.com")).isFalse();
    }
}
