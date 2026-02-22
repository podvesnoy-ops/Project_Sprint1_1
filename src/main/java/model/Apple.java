package model;

import model.constants.Colour;
import model.constants.Discount;

public class Apple extends Food {
    private String colour; // Цвет яблок 11 + 22

    public Apple(int amount, double price, String colour) {
        this.amount = amount;
        this.price = price;
        this.colour = colour;
        this.isVegetarian = true;
    }

    public String getColour() {
        return colour;
    }

    @Override
    public double getDiscount() {
        // Для красных яблок скидка 60%
        if (Colour.RED.equalsIgnoreCase(colour)) {
            return Discount.DISCOUNT_FOR_RED_APPLE;
        }
        return 0;
    }
}
