package shopping.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    final int NAME_LENGTH =15;
    final int IMG_URL_LENGTH = 500;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = NAME_LENGTH)
    private String name;

    @Column
    private int price;

    @Column
    private int stock;

    @Column(nullable = false, length = IMG_URL_LENGTH)
    private String imgUrl;

    public Product(String name, int price, int stock, String imgUrl){
        validateNotBlank(name, "이름은 필수 입력 항목입니다.");
        validateNotBlank(imgUrl, "이미지 주소는 필수 입력 항목입니다.");
        checkStringLength(name, NAME_LENGTH, "상품명은 15자를 초과할 수 없습니다.");
        checkStringLength(imgUrl, IMG_URL_LENGTH, "이미지 주소는 500자를 초과할 수 없습니다.");
        checkSymbol(name, "한글, 영어, 숫자, (), [], +, -, &, /, _ 외의 특수문자는 사용불가");
        checkIntSize(price, "가격은 0 이상이여야 합니다.");
        checkIntSize(stock, "재고는 0 이상이여야 합니다.");
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.imgUrl = imgUrl;
    }

    private void checkSymbol(String name, String message){
        if (!name.matches("[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]+")){
            throw new IllegalArgumentException(message);
        }
    }

    private void checkStringLength(String str, int size, String message){
        if (str.length() > size)
            throw new IllegalArgumentException(message);
    }

    private void checkIntSize(int num, String message){
        if (0 > num)
            throw new IllegalArgumentException(message);
    }

    private void validateNotBlank(String value, String message){
        if(value == null || value.isBlank())
            throw new IllegalArgumentException(message);
    }
}
