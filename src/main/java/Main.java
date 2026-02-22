import database.DatabaseManager;
import model.Apple;
import model.Food;
import model.Meat;
import model.constants.Colour;
import service.ShoppingCart;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static DatabaseManager dbManager = new DatabaseManager();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Инициализируем БД при старте
        dbManager.initializeDatabase();

        while (true) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // очистка буфера после nextInt()

            switch (choice) {
                case 1:
                    addProductManually();
                    break;
                case 2:
                    addSampleProducts();
                    break;
                case 3:
                    showCart();
                    break;
                case 4:
                    clearCart();
                    break;
                case 5:
                    deleteProduct();
                    break;
                case 0:
                    System.out.println("До свидания!");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== МЕНЮ КОРЗИНЫ ===");
        System.out.println("1. Добавить продукт вручную");
        System.out.println("2. Добавить тестовые продукты");
        System.out.println("3. Показать корзину и расчеты");
        System.out.println("4. Очистить корзину");
        System.out.println("5. Удалить продукт (по индексу)");
        System.out.println("0. Выход");
        System.out.print("Ваш выбор: ");
    }

    private static void addProductManually() {
        System.out.println("\nТип продукта:");
        System.out.println("1. Мясо");
        System.out.println("2. Яблоки");
        System.out.print("Выберите тип: ");
        int type = scanner.nextInt();

        System.out.print("Количество (кг): ");
        int amount = scanner.nextInt();

        System.out.print("Цена за кг: ");
        double price = scanner.nextDouble();

        if (type == 1) {
            dbManager.addProduct(new Meat(amount, price));
            System.out.println("Мясо добавлено!");
        } else if (type == 2) {
            scanner.nextLine(); // очистка
            System.out.print("Цвет (red/green): ");
            String colour = scanner.nextLine();
            dbManager.addProduct(new Apple(amount, price, colour));
            System.out.println("Яблоки добавлены!");
        }
    }

    private static void addSampleProducts() {
        // Те же продукты, что и в исходной программе
        Meat meat = new Meat(5, 100);
        Apple redApple = new Apple(10, 50, Colour.RED);
        Apple greenApple = new Apple(8, 60, Colour.GREEN);

        dbManager.addProduct(meat);
        dbManager.addProduct(redApple);
        dbManager.addProduct(greenApple);

        System.out.println("Тестовые продукты добавлены в БД");
    }

    private static void showCart() {
        List<Food> products = dbManager.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("Корзина пуста");
            return;
        }

        System.out.println("\n=== СОДЕРЖИМОЕ КОРЗИНЫ ===");
        for (int i = 0; i < products.size(); i++) {
            Food product = products.get(i);
            System.out.print((i + 1) + ". ");
            if (product instanceof Apple) {
                Apple apple = (Apple) product;
                System.out.printf("Яблоки (%s): %d кг по %.2f руб/кг, скидка: %.0f%%\n",
                        apple.getColour(), apple.getAmount(), apple.getPrice(), apple.getDiscount());
            } else if (product instanceof Meat) {
                System.out.printf("Мясо: %d кг по %.2f руб/кг\n",
                        product.getAmount(), product.getPrice());
            }
        }

        // Создаем корзину из списка продуктов
        Food[] items = products.toArray(new Food[0]);
        ShoppingCart cart = new ShoppingCart(items);

        System.out.println("\n=== РАСЧЕТЫ ===");
        System.out.printf("Общая сумма без скидки: %.2f руб\n", cart.getTotalPrice());
        System.out.printf("Общая сумма со скидкой: %.2f руб\n", cart.getTotalPriceWithDiscount());
        System.out.printf("Сумма вегетарианских продуктов: %.2f руб\n", cart.getTotalVegetarianPrice());
    }

    private static void clearCart() {
        dbManager.clearProducts();
        System.out.println("Корзина очищена");
    }

    private static void deleteProduct() {
        List<Food> products = dbManager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Корзина пуста, нечего удалять");
            return;
        }

        System.out.println("\nКакой продукт удалить? (введите номер):");
        for (int i = 0; i < products.size(); i++) {
            Food product = products.get(i);
            System.out.print((i + 1) + ". ");
            if (product instanceof Apple) {
                Apple apple = (Apple) product;
                System.out.printf("Яблоки (%s) - %d кг\n", apple.getColour(), apple.getAmount());
            } else if (product instanceof Meat) {
                System.out.printf("Мясо - %d кг\n", product.getAmount());
            }
        }

        int index = scanner.nextInt();
        if (index >= 1 && index <= products.size()) {
            // В нашей простой реализации удаляем все и добавляем заново без выбранного
            // В реальном проекте лучше добавить метод deleteByIndex в DatabaseManager
            System.out.println("Функция удаления будет добавлена позже");
        }
    }
}