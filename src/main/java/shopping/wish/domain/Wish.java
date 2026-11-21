package shopping.wish.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import shopping.common.error.ErrorCode;
import shopping.common.error.ShoppingException;
import shopping.product.domain.Product;

@Entity
@Table(name = "wishes")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long memberId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected Wish() {
    }

    public Wish(Long memberId, Product product) {
        if (memberId == null || product == null) {
            throw new IllegalArgumentException("회원과 상품은 필수입니다.");
        }

        this.memberId = memberId;
        this.product = product;
    }

    public void requireOwner(Long requesterId) {
        if (!memberId.equals(requesterId)) {
            throw new ShoppingException(ErrorCode.FORBIDDEN);
        }
    }

    public Long id() {
        return id;
    }

    public Product product() {
        return product;
    }
}
