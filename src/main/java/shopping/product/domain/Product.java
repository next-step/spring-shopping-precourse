package shopping.product.domain;

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

    public Product(ProductName name, ProductPrice price, String imageUrl) {
        this.name = name.getValue();
        this.price = price.getValue();
        this.imageUrl = imageUrl;
    }

    public void update(ProductName name, ProductPrice price, String imageUrl) {
        this.name = name.getValue();
        this.price = price.getValue();
        this.imageUrl = imageUrl;
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
