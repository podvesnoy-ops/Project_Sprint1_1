package service;

import model.Food;

public class ShoppingCart {
    private Food[] items;

    public ShoppingCart(Food[] items) {
        this.items = items;
    }

    // Сумма товаров в корзине без скидки
    public double getTotalPrice() {
        double total = 0;
        for (Food item : items) {
            total += item.getAmount() * item.getPrice();
        }
        return total;
    }

    // Сумма товаров в корзине со скидкой
    public double getTotalPriceWithDiscount() {
        double total = 0;
        for (Food item : items) {
            double price = item.getAmount() * item.getPrice();
            double discount = item.getDiscount(); // скидка
            total += price * (100 - discount) / 100; // минус скидка
        }
        return total;
    }

    // Сумма вегетарианских продуктов в корзине без скидки
    public double getTotalVegetarianPrice() {
        double total = 0;
        for (Food item : items) {
            if (item.isVegetarian()) {
                total += item.getAmount() * item.getPrice();
            }
        }
        return total;
    }
}