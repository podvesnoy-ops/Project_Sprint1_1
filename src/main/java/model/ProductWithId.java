package model;

public class ProductWithId {
    private int id;
    private Food product;

    public ProductWithId(int id, Food product) {
        this.id = id;
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public Food getProduct() {
        return product;
    }
}