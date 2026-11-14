package shopping.domain.entity;

import java.util.regex.Pattern;

public class ImageUrl {

    private final String regex = "^(https?:\\/\\/.*\\.(png|jpg))$";
    private final Pattern pattern = Pattern.compile(regex);

    private String value;

    public ImageUrl(String value) throws IllegalArgumentException{
        if(!pattern.matcher(value).matches())
            throw new IllegalArgumentException("잘못된 이미지 경로입니다.");

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
