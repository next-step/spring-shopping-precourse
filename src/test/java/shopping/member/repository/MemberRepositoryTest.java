package shopping.member.repository;

import kotlin.OptIn;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import shopping.member.entity.Member;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 이메일로_회원을_조회한다() {
        memberRepository.save(new Member("gildong@josun.kr", "fatherMyfather12"));
        Optional<Member> found = memberRepository.findByEmail("gildong@josun.kr");

        assertThat(found).isPresent();
    }

    @Test
    void 없는_이메일로_조회하면_비어있다() {
        Optional<Member> found = memberRepository.findByEmail("notfound@error.not");
        assertThat(found).isEmpty();
    }

    @Test
    void 가입된_이메일이면_existsByEmail이_true다() {
        memberRepository.save(new Member("gildong@josun.kr", "fatherMyfather12"));

        assertThat(memberRepository.existsByEmail("gildong@josun.kr")).isTrue();
        assertThat(memberRepository.existsByEmail("b@b.com")).isFalse();
    }

    @Test
    void 같은_이메일로_저장하면_예외가_발생한다() {
        memberRepository.save(new Member("gildong@josun.kr", "fatherMyfather12"));

        assertThatThrownBy(()->
                memberRepository.saveAndFlush(new Member("gildong@josun.kr", "fatherMyfather12")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}