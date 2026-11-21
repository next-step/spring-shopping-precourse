package shopping.model;

public class Wish {
    private Long id;
    private final Member member;
    private final Product product;

    public Wish(Member member, Product product) {
        if (member == null || product == null) {
            throw new IllegalArgumentException(
                    "위시리스트 정보는 null일 수 없습니다");
        }
        this.member = member;
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public boolean belongsTo(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("회원 ID는 null일 수 없습니다");
        }
        return member.getId().equals(memberId);
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("위시리스트 ID는 null일 수 없습니다");
        }
        this.id = id;
    }
}
