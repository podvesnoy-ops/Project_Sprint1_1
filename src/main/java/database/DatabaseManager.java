package database;

import model.Apple;
import model.Food;
import model.Meat;
import model.ProductWithId;  // добавили импорт
import model.constants.Colour;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Файл базы данных будет в корне проекта
    private static final String DB_URL = "jdbc:sqlite:shop.db";

    // Инициализация базы данных
    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type TEXT NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "colour TEXT, " +
                "is_vegetarian INTEGER NOT NULL" +
                ")";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("База данных SQLite инициализирована");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    // Добавление продукта
    public void addProduct(Food product) {
        String sql = "INSERT INTO products (type, amount, price, colour, is_vegetarian) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (product instanceof Apple) {
                pstmt.setString(1, "apple");
                pstmt.setString(4, ((Apple) product).getColour());
            } else if (product instanceof Meat) {
                pstmt.setString(1, "meat");
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                return;
            }

            pstmt.setInt(2, product.getAmount());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setBoolean(5, product.isVegetarian());

            pstmt.executeUpdate();
            System.out.println("Продукт добавлен в БД");

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении продукта: " + e.getMessage());
        }
    }

    // Получение всех продуктов (без ID - старый метод)
    public List<Food> getAllProducts() {
        List<Food> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String type = rs.getString("type");
                int amount = rs.getInt("amount");
                double price = rs.getDouble("price");

                if ("apple".equals(type)) {
                    String colour = rs.getString("colour");
                    products.add(new Apple(amount, price, colour));
                } else if ("meat".equals(type)) {
                    products.add(new Meat(amount, price));
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при чтении продуктов: " + e.getMessage());
        }

        return products;
    }

    // НОВЫЙ МЕТОД: Получение продуктов с ID
    public List<ProductWithId> getAllProductsWithId() {
        List<ProductWithId> products = new ArrayList<>();
        String sql = "SELECT id, type, amount, price, colour FROM products";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                int amount = rs.getInt("amount");
                double price = rs.getDouble("price");

                if ("apple".equals(type)) {
                    String colour = rs.getString("colour");
                    products.add(new ProductWithId(id, new Apple(amount, price, colour)));
                } else if ("meat".equals(type)) {
                    products.add(new ProductWithId(id, new Meat(amount, price)));
                }
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при чтении продуктов: " + e.getMessage());
        }

        return products;
    }

    // Очистка таблицы
    public void clearProducts() {
        String sql = "DELETE FROM products";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Таблица очищена");
        } catch (SQLException e) {
            System.out.println("Ошибка при очистке: " + e.getMessage());
        }
    }

    // НОВЫЙ МЕТОД: Удаление продукта по ID
    public void deleteProductById(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int deleted = pstmt.executeUpdate();

            if (deleted > 0) {
                System.out.println("Продукт с ID " + id + " удален");
            } else {
                System.out.println("Продукт с ID " + id + " не найден");
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении: " + e.getMessage());
        }
    }
}