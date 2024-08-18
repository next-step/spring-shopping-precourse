package shopping.service;

import org.springframework.stereotype.Service;
import shopping.model.Member;
import shopping.repository.MemberRepository;

import java.util.Optional;

@Service
public interface MemberService {

    void registerMember(String email, String rawPassword);

    public boolean authenticateMember(String email, String rawPassword);

    public Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);
}

