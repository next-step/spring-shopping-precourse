package shopping.domain.entity;

import java.util.concurrent.atomic.AtomicLong;

public class Product {

    private Long id;
    private Name name;
    private Price price;
    private ImageUrl imageUrl;

    public Product(Long id, Product product) {
        this.id = id;
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
    }

    public Product(Long id, Name name, Price price, ImageUrl imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(Name name, Price price, ImageUrl imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public ImageUrl getImageUrl() {
        return imageUrl;
    }
}
