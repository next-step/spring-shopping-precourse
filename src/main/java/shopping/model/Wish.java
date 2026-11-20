package shopping.model;

public class Wish {
    private Long id;
    private final Member member;
    private final Product product;

    public Wish(Member member, Product product) {
        this.member = member;
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public boolean belongsTo(Long memberId) {
        return member.getId().equals(memberId);
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        this.id = id;
    }
}
