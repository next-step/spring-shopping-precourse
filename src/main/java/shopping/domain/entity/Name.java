package shopping.domain.entity;

import java.util.regex.Pattern;

public class Name {

    private final String regx = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]{1,15}$";
    private final Pattern pattern = Pattern.compile(regx);
    private String value;

    public Name(String value) throws IllegalArgumentException{
        if(!pattern.matcher(value).matches())
            throw new IllegalArgumentException("상품 명이 올바르지 않습니다.");

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
