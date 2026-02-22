import database.DatabaseManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Apple;
import model.Food;
import model.Meat;
import model.ProductWithId;
import model.constants.Colour;
import service.ShoppingCart;

import java.util.List;

public class MainFX extends Application {

    private DatabaseManager dbManager;
    private TableView<ProductDisplay> table;
    private ObservableList<ProductDisplay> productData;
    private Label totalLabel;
    private Label totalWithDiscountLabel;
    private Label vegetarianLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();

        primaryStage.setTitle("Проект 2 Спринта с GUI");

        // Создаем корневой макет
        BorderPane root = new BorderPane();

        // Верхняя панель с кнопками
        ToolBar toolBar = createToolBar();
        root.setTop(toolBar);

        // Центральная таблица
        table = createTable();
        root.setCenter(table);

        // Нижняя панель с итогами
        VBox bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);

        // Загружаем данные
        refreshTable();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ToolBar createToolBar() {
        Button addMeatBtn = new Button("➕ Добавить мясо");
        Button addAppleBtn = new Button("🍎 Добавить яблоки");
        Button refreshBtn = new Button("🔄 Обновить");
        Button clearBtn = new Button("🗑 Очистить корзину");
        Button deleteBtn = new Button("❌ Удалить выбранное");

        addMeatBtn.setOnAction(e -> showAddMeatDialog());
        addAppleBtn.setOnAction(e -> showAddAppleDialog());
        refreshBtn.setOnAction(e -> refreshTable());
        clearBtn.setOnAction(e -> clearCart());
        deleteBtn.setOnAction(e -> deleteSelected());

        ToolBar toolBar = new ToolBar(addMeatBtn, addAppleBtn,
                new Separator(),
                refreshBtn, deleteBtn,
                new Separator(),
                clearBtn);
        return toolBar;
    }

    private TableView<ProductDisplay> createTable() {
        TableView<ProductDisplay> table = new TableView<>();

        // Колонка типа
        TableColumn<ProductDisplay, String> typeCol = new TableColumn<>("Тип");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        // Колонка цвета (для яблок)
        TableColumn<ProductDisplay, String> colourCol = new TableColumn<>("Цвет");
        colourCol.setCellValueFactory(new PropertyValueFactory<>("colour"));
        colourCol.setPrefWidth(80);

        // Колонка количества
        TableColumn<ProductDisplay, Integer> amountCol = new TableColumn<>("Количество (кг)");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);

        // Колонка цены за кг
        TableColumn<ProductDisplay, Double> priceCol = new TableColumn<>("Цена за кг");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);

        // Колонка скидки
        TableColumn<ProductDisplay, Double> discountCol = new TableColumn<>("Скидка %");
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discount"));
        discountCol.setPrefWidth(80);

        // Колонка итоговой цены
        TableColumn<ProductDisplay, Double> totalCol = new TableColumn<>("Итого");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalCol.setPrefWidth(100);

        table.getColumns().addAll(typeCol, colourCol, amountCol, priceCol, discountCol, totalCol);

        // Добавляем контекстное меню
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(e -> deleteSelected());
        contextMenu.getItems().add(deleteItem);
        table.setContextMenu(contextMenu);

        return table;
    }

    private VBox createBottomPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1 0 0 0;");

        totalLabel = new Label("Общая сумма без скидки: 0.00 руб");
        totalWithDiscountLabel = new Label("Общая сумма со скидкой: 0.00 руб");
        vegetarianLabel = new Label("Сумма вегетарианских продуктов: 0.00 руб");

        totalLabel.setStyle("-fx-font-weight: bold;");
        totalWithDiscountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
        vegetarianLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");

        vbox.getChildren().addAll(totalLabel, totalWithDiscountLabel, vegetarianLabel);

        return vbox;
    }

    private void showAddMeatDialog() {
        Dialog<Meat> dialog = new Dialog<>();
        dialog.setTitle("Добавить мясо");
        dialog.setHeaderText("Введите параметры мяса");

        // Кнопки
        ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Поля ввода
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        amountField.setPromptText("Количество (кг)");
        TextField priceField = new TextField();
        priceField.setPromptText("Цена за кг");

        grid.add(new Label("Количество (кг):"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Цена за кг:"), 0, 1);
        grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Валидация
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int amount = Integer.parseInt(amountField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    return new Meat(amount, price);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Введите корректные числа");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(meat -> {
            dbManager.addProduct(meat);
            refreshTable();
        });
    }

    private void showAddAppleDialog() {
        Dialog<Apple> dialog = new Dialog<>();
        dialog.setTitle("Добавить яблоки");
        dialog.setHeaderText("Введите параметры яблок");

        ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        amountField.setPromptText("Количество (кг)");
        TextField priceField = new TextField();
        priceField.setPromptText("Цена за кг");

        ComboBox<String> colourBox = new ComboBox<>();
        colourBox.getItems().addAll(Colour.RED, Colour.GREEN);
        colourBox.setValue(Colour.RED);

        grid.add(new Label("Количество (кг):"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Цена за кг:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Цвет:"), 0, 2);
        grid.add(colourBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int amount = Integer.parseInt(amountField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    String colour = colourBox.getValue();
                    return new Apple(amount, price, colour);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Введите корректные числа");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(apple -> {
            dbManager.addProduct(apple);
            refreshTable();
        });
    }

    private void refreshTable() {
        productData = FXCollections.observableArrayList();
        List<ProductWithId> productsWithId = dbManager.getAllProductsWithId();

        for (ProductWithId p : productsWithId) {
            productData.add(new ProductDisplay(p.getId(), p.getProduct()));
        }

        table.setItems(productData);
        updateTotals();
    }

    private void updateTotals() {
        List<Food> products = dbManager.getAllProducts();
        if (!products.isEmpty()) {
            Food[] items = products.toArray(new Food[0]);
            ShoppingCart cart = new ShoppingCart(items);

            totalLabel.setText(String.format("Общая сумма без скидки: %.2f руб", cart.getTotalPrice()));
            totalWithDiscountLabel.setText(String.format("Общая сумма со скидкой: %.2f руб", cart.getTotalPriceWithDiscount()));
            vegetarianLabel.setText(String.format("Сумма вегетарианских продуктов: %.2f руб", cart.getTotalVegetarianPrice()));
        } else {
            totalLabel.setText("Общая сумма без скидки: 0.00 руб");
            totalWithDiscountLabel.setText("Общая сумма со скидкой: 0.00 руб");
            vegetarianLabel.setText("Сумма вегетарианских продуктов: 0.00 руб");
        }
    }

    private void clearCart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Очистка корзины");
        alert.setHeaderText("Вы уверены, что хотите очистить корзину?");
        alert.setContentText("Все продукты будут удалены из базы данных");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dbManager.clearProducts();
                refreshTable();
            }
        });
    }

    private void deleteSelected() {
        ProductDisplay selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Подтверждение удаления");
            confirm.setHeaderText("Удалить продукт?");
            confirm.setContentText(selected.getType() + " - " + selected.getAmount() + " кг");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    dbManager.deleteProductById(selected.getId());
                    refreshTable();
                }
            });
        } else {
            showAlert("Ошибка", "Выберите продукт для удаления");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Вспомогательный класс для отображения в таблице
    public static class ProductDisplay {
        private final int id;
        private final String type;
        private final String colour;
        private final int amount;
        private final double price;
        private final double discount;
        private final double totalPrice;

        public ProductDisplay(int id, Food product) {
            this.id = id;
            if (product instanceof Apple) {
                Apple apple = (Apple) product;
                this.type = "Яблоки";
                this.colour = apple.getColour();
                this.discount = apple.getDiscount();
            } else if (product instanceof Meat) {
                this.type = "Мясо";
                this.colour = "-";
                this.discount = 0;
            } else {
                this.type = "Неизвестно";
                this.colour = "-";
                this.discount = 0;
            }
            this.amount = product.getAmount();
            this.price = product.getPrice();
            this.totalPrice = amount * price * (100 - discount) / 100;
        }

        // Геттеры для PropertyValueFactory
        public int getId() { return id; }
        public String getType() { return type; }
        public String getColour() { return colour; }
        public int getAmount() { return amount; }
        public double getPrice() { return price; }
        public double getDiscount() { return discount; }
        public double getTotalPrice() { return totalPrice; }
    }
}