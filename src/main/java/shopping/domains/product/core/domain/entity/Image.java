package shopping.domains.product.core.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Image {
    public static final String REG_EXP = "^(http|https):\\/\\/[^\\s]+\\/.*\\.(jpg|jpeg|png|gif|bmp|svg)$";
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);

    private String downloadUrl;

    public Image(@NonNull final String downloadUrl) {
        validate(downloadUrl);

        this.downloadUrl = downloadUrl.trim();
    }

    private void validate(@NonNull final String url) {
        if(PATTERN.matcher(url.trim()).matches()) {
            return;
        }
        throw new IllegalArgumentException("잘못된 이미지 경로이거나 지원하지 않는 이미지 형식입니다.");
    }
}
