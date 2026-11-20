package shopping.product;

public class ProductName {

    private static final int MAX = 15;
    private static final String OK = "^[가-힣a-zA-Z0-9 ()\\[\\]+&/_-]*$";

    private final String value;

    public ProductName(String value) {
        checkBlank(value);
        checkLength(value);
        checkChars(value);
        this.value = value;
    }

    private void checkBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다.");
        }
    }

    private void checkLength(String value) {
        if (value.length() > MAX) {
            throw new IllegalArgumentException("이름은 15글자를 초과할 수 없습니다.");
        }
    }

    private void checkChars(String value) {
        if (!value.matches(OK)) {
            throw new IllegalArgumentException("허용되지 않은 특수 문자가 있습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
