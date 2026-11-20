package shopping.wish.infrastructure;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import shopping.wish.application.WishRepository;
import shopping.wish.domain.Wish;

@Repository
public class JdbcWishRepository implements WishRepository {
    private final JdbcClient jdbc;

    public JdbcWishRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<ListedWish> findByMemberId(long memberId) {
        return jdbc.sql("""
                SELECT w.id AS wish_id, p.id AS product_id, p.name, p.price, p.image_url
                FROM wishes w JOIN products p ON w.product_id = p.id
                WHERE w.member_id = :memberId ORDER BY w.id
                """)
                .param("memberId", memberId)
                .query((row, index) -> new ListedWish(row.getLong("wish_id"),
                        row.getLong("product_id"), row.getString("name"),
                        new BigDecimal(row.getString("price")), row.getString("image_url")))
                .list();
    }

    @Override
    public Optional<Long> add(Wish wish) {
        return jdbc.sql("""
                INSERT INTO wishes (member_id, product_id)
                SELECT :memberId, id FROM products WHERE id = :productId
                ON CONFLICT (member_id, product_id) DO NOTHING RETURNING id
                """)
                .param("memberId", wish.memberId()).param("productId", wish.productId())
                .query(Long.class).optional();
    }

    @Override
    public boolean delete(long memberId, long wishId) {
        return jdbc.sql("DELETE FROM wishes WHERE member_id = :memberId AND id = :wishId")
                .param("memberId", memberId).param("wishId", wishId)
                .update() > 0;
    }
}
