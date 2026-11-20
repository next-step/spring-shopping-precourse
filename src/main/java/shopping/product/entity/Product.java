package shopping.product.entity;

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
    private int count;
    private String url;

    protected Product() {}

    public Product(String name, int price, int count, String url){
        this.name=name;
        this.price=price;
        this.count=count;
        this.url=url;
    }

    public void update(String name, int price, int count, String url) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.url = url;
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

    public int getCount() {
        return count;
    }

    public String getUrl() {
        return url;
    }
}
