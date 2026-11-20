package shopping.model;

public class Product {
    private Long id;
    private ProductName name;
    private Long price;
    private String imageUrl;

    public Product(ProductName name, Long price, String imageUrl) {
        validateFields(name, price, imageUrl);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void update(ProductName name, Long price, String imageUrl) {
        validateFields(name, price, imageUrl);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    private void validateFields(ProductName name, Long price, String imageUrl) {
        if (name == null || price == null || imageUrl == null) {
            throw new IllegalArgumentException("상품 정보는 null일 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public void assignId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("상품 ID는 null일 수 없습니다");
        }
        this.id = id;
    }

    public String getName() {
        return name.value();
    }

    public Long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
