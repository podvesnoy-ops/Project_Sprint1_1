package model;

public abstract class Food  implements Discountable {

    protected int amount;          // количество
    protected double price;        // цена
    protected boolean isVegetarian; // вегетарианский?

    // Геттеры
    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    @Override
    public double getDiscount() {
        // По умолчанию скидка 0%
        return 0;
    }
}
