package shopping.api.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shopping.domain.entity.Name;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NameTest {

    @Test
    @DisplayName("이름은 15자를 넘으면 안되고, 특수문자를 일부 허용한다.")
    void constructorTest() {
        try {
            new Name("안녕하세요감사해요잘있어요다시만나요안녕하세요");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            assertEquals("상품 명이 올바르지 않습니다.", e.getMessage());
        }

    }
}
