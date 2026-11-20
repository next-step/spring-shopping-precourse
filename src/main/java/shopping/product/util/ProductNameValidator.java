package shopping.product.util;

public final class ProductNameValidator {
    private static final String ALLOWED_SPECIAL_CHARACTERS = "()[]+-&/_";
    private static final String NORMAL_CHARACTERS_REGEX = "[a-zA-Z0-9가-힣\\s]";

    private ProductNameValidator() {
    }

    public static void validate(String name) {
        String specialCharacters = name.replaceAll(NORMAL_CHARACTERS_REGEX, "");
        for (char character : specialCharacters.toCharArray()) {
            validateAllowed(character);
        }
    }

    private static void validateAllowed(char character) {
        if (ALLOWED_SPECIAL_CHARACTERS.indexOf(character) == -1) {
            throw new IllegalArgumentException("상품 이름에 허용되지 않은 특수문자가 포함되어 있습니다: " + character);
        }
    }
}
