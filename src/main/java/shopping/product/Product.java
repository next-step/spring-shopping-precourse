package shopping.product;

public class Product {
    private ProductName name;
    private Price price;
    private ImageUrl imageUrl;

    public Product(ProductName name, Price price, ImageUrl imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void update(ProductName name, Price price, ImageUrl imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name.getValue();
    }

    public int getPrice() {
        return price.getValue();
    }


    public String getImageUrl() {
        return imageUrl.getValue();
    }

}
