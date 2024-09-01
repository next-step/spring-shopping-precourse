package shopping.api.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.domain.entity.ImageUrl;

import static org.assertj.core.api.Assertions.*;

public class ImageUrlTest {

    @Test
    @DisplayName("이미지 경로는 http://나 https://로 시작하고 이미지 확장자로 끝나야 한다. (.png, ~~~)")
    void constructorTest() {

//        assertThat(new ImageUrl("www.naver.com").getValue()).isEqualTo(new ImageUrl("www.naver.com").getValue());
        assertThatIllegalArgumentException().isThrownBy(() -> new ImageUrl("http://www.naver.com/image"));
    }
}
