package shopping.domain.entity;

public class Price {

    private int value;

    public Price(int value) throws IllegalArgumentException{
        if(value < 0)
            throw new IllegalArgumentException("상품 가격은 음수가 될 수 없습니다.");
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
