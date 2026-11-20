package shopping;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.datasource.password=",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
class WishRepositoryTest {

    @Autowired private WishRepository wishes;

    @Autowired private MemberRepository members;

    @Autowired private ProductRepository products;

    @Test
    void databaseRejectsDuplicateMemberProductPair() {
        Member member = members.save(new Member("owner@example.com", "hash"));
        Product product = products.save(new Product("keyboard", 1000, "https://example.com/a.jpg"));
        wishes.saveAndFlush(new Wish(member, product));
        assertThatThrownBy(() -> wishes.saveAndFlush(new Wish(member, product)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
