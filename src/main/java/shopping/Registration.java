package shopping;

public final class Registration {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordHashing;

    public Registration(
            MemberRepository memberRepository,
            PasswordEncoder passwordHashing
    ) {
        this.memberRepository = memberRepository;
        this.passwordHashing = passwordHashing;
    }

    public Member member(Email email, String rawPassword) {
        if (memberRepository.member(email) != null) {
            throw new IllegalArgumentException("이미 가입된 이메일");
        }

        Password password =
                Password.create(rawPassword, passwordHashing);

        return memberRepository.registeredMember(email, password);
    }
}
