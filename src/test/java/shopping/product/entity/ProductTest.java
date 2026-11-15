package shopping.product.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import shopping.member.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


class ProductTest {

    @Test
    void 정상적으로_상품등록() {
        Product product = new Product(
                "돼지뚱냥이",
                100_000,
                1,
                "https://i.ibb.co/BVQ48J5F/cat.jpg");

        assertThat(product)
                .extracting(Product::getName, Product::getPrice, Product::getStock, Product::getImgUrl)
                .containsExactly("돼지뚱냥이", 100_000, 1, "https://i.ibb.co/BVQ48J5F/cat.jpg");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void 이름_없는_상품등록(String name) {
        assertThatThrownBy(() -> new Product(
                name,
                10_000,
                10,
                "https://i.ibb.co/BVQ48J5F/cat.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름은 필수 입력 항목입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void 이미지주소_없는_상품등록(String imgSrc) {
        assertThatThrownBy(() -> new Product(
                "돼지뚱냥이",
                10_000,
                10,
                imgSrc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 주소는 필수 입력 항목입니다.");
    }

    @Test
    void 상품명이_15글자를_초과하는_상품등록() {
        assertThatThrownBy(() -> new Product(
                "돼지뚱냥이통대창고등어지방간삼색해삼덩어리",
                10_000,
                10,
                "https://i.ibb.co/BVQ48J5F/cat.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품명은 15자를 초과할 수 없습니다.");
    }

    @Test
    void 이미지주소가_500자를_초과하는_상품등록() {
        assertThatThrownBy(() -> new Product(
                "돼지뚱냥이",
                10_000,
                10,
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAFUlEQVR42mNkYPj/n4EIwDiQkL4KAVsSAf1/w+4EAAAAAElFTkSuQmCC#padding_comment_to_make_this_data_uri_exceed_five_hundred_characters_for_testing_purposes_abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_long_string_validation_check_0123456789_abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789_abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789_abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ_end_of_data_url_string_test_complete_now_1234567890_extra_padding_characters_added_here\n"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미지 주소는 500자를 초과할 수 없습니다.");
    }

    @Test
    void 사용불가_특수문자를_이용한_상품등록() {
        assertThatThrownBy(() -> new Product(
                "돼지뚱냥이^^7",
                10_000,
                10,
                "https://i.ibb.co/BVQ48J5F/cat.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("한글, 영어, 숫자, (), [], +, -, &, /, _ 외의 특수문자는 사용불가");
    }


    @Test
    void 음수_가격을_지닌_상품등록() {
        assertThatThrownBy(() -> new Product(
                "돼지뚱냥이",
                -10_000,
                10,
                "https://i.ibb.co/BVQ48J5F/cat.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 양수여야합니다.");
    }
    @Test
    void 경계값_0_가격을_지닌_상품등록() {
        Product product = new Product(
                "돼지뚱냥이",
                0,
                10,
                "https://i.ibb.co/BVQ48J5F/cat.jpg");
        assertThat(product)
                .extracting(Product::getName, Product::getPrice, Product::getStock, Product::getImgUrl)
                .containsExactly("돼지뚱냥이", 0, 10, "https://i.ibb.co/BVQ48J5F/cat.jpg");

    }

    @Test
    void 음수_재고를_지닌_상품등록() {
        assertThatThrownBy(() -> new Product(
                "돼지뚱냥이",
                10_000,
                -10,
                "https://i.ibb.co/BVQ48J5F/cat.jpg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고는 양수여야합니다.");
    }

    @Test
    void 경계값_0_재고를_지닌_상품등록() {
        Product product = new Product(
                "돼지뚱냥이",
                10_000,
                0,
                "https://i.ibb.co/BVQ48J5F/cat.jpg");
        assertThat(product)
                .extracting(Product::getName, Product::getPrice, Product::getStock, Product::getImgUrl)
                .containsExactly("돼지뚱냥이", 10_000, 0, "https://i.ibb.co/BVQ48J5F/cat.jpg");
    }

}