package shopping.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopping.model.Member;
import shopping.repository.MemberRepository;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public void registerMember(String email, String rawPassword) {
        Member member = new Member(email, rawPassword);
        memberRepository.save(member);
    }

    @Override
    public boolean authenticateMember(String email, String rawPassword) {
        Optional<Member> memberOpt = memberRepository.findByEmail(email);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            // 저장된 비밀번호와 입력된 비밀번호를 평문으로 비교
            return rawPassword.equals(member.getPassword());
        }
        return false;
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
