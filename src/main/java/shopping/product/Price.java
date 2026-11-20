package shopping.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Price {
    private static final int MIN_VALUE = 0;
    @Column(name = "price", nullable = false)
    private int value;
    

    protected Price() {
    }

    public Price(int value) {
        validateNotNegative(value);
        this.value = value;
    }

    private void validateNotNegative(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
    }

    public int getValue() {
        return value;
    }
    // 가격을 비교하기 위한 오버라이드를 하는 이유. 1000원과 1000원이 같은지 왜 비교하는가
    // 가격을 생성할때 1000원을 한번 생성하고 한번더 1000원을 생성했을때 이게 없다면 둘은 다른 객체로 인식.
    // 그렇다면 값에 대한 비교를 하게 되는 경우 1000원과 1000원, 10000원이 있을때 셋 모두 다른 값으로 인식하게 된다. 
    // 오버라이드 후 equals를 통해 1000원과 1000원을 같은 값으로 인식하게 할수 있음.
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Price other)) { return false; }

        return value == other.value;
    }
    @Override 
    public int hashCode() {
        return Integer.hashCode(value);
    }
}