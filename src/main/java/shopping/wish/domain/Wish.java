package shopping.wish.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import shopping.product.domain.Product;

@Entity
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    protected Wish(){
    }

    public Wish(Long id, Long memberId, Product product) {
        this.id = id;
        this.memberId = memberId;
        this.product = product;
    }

    public boolean isOwnedBy(Long otherMemberId) {
        return memberId.equals(otherMemberId);
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Product getProduct() {
        return product;
    }
}
