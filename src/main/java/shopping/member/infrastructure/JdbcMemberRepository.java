package shopping.member.infrastructure;

import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import shopping.member.application.MemberRepository;
import shopping.member.domain.Member;

@Repository
public class JdbcMemberRepository implements MemberRepository {
    private final JdbcClient jdbc;

    public JdbcMemberRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Long> save(String email, String passwordHash) {
        return jdbc.sql("""
                INSERT INTO members (email, password_hash) VALUES (:email, :hash)
                ON CONFLICT (email) DO NOTHING RETURNING id
                """)
                .param("email", email).param("hash", passwordHash)
                .query(Long.class).optional();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return jdbc.sql("SELECT id, email, password_hash FROM members WHERE email = :email")
                .param("email", email)
                .query((row, index) -> new Member(
                        row.getLong("id"), row.getString("email"), row.getString("password_hash")))
                .optional();
    }
}
