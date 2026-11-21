package shopping.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private String imageUrl;

    protected Product() {
    }

    public Product(ProductName name, Price price, ImageUrl imageUrl) {
        this.name = name.value();
        this.price = price.value();
        this.imageUrl = imageUrl.value();
    }

    public void update(ProductName name, Price price, ImageUrl imageUrl) {
        this.name = name.value();
        this.price = price.value();
        this.imageUrl = imageUrl.value();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
