package shopping;

public final class Login {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Login(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member member(Email email, String rawPassword) {
        Member member = memberRepository.member(email);
        if (member == null || !member.password().matches(rawPassword, passwordEncoder)) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다");
        }
        return member;
    }
}
