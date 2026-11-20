package shopping.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Product product;

    private Wish(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

    public static Wish of(Member member, Product product) {
        Assert.notNull(member, "member must not be null");
        Assert.notNull(product, "product must not be null");
        return new Wish(member, product);
    }

    public boolean isOwnedBy(Member other) {
        if (other == null) {
            return false;
        }

        return member.equals(other);
    }
}
