package shopping.model;

public record ProductName(String value) {
    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 15;
    public static final String PATTERN = "[\\p{L}\\p{N} ()\\[\\]+&/_-]+";

    public ProductName {
        if (value == null || value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "상품 이름은 1자 이상 15자 이하여야 합니다");
        }
        if (!value.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "상품 이름에 허용되지 않은 문자가 있습니다");
        }
    }
}
