package controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import static database.DatabaseFunctions.login;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Admin
 */
public class FarmerPageController implements Initializable {

    ObservableList<Products> listProducts = FXCollections.observableArrayList();
    ObservableList<Orders> listOrders = FXCollections.observableArrayList();
    @FXML
    private Label userNameLabel;
    @FXML
    private StackPane motherPane;
    @FXML
    private TableColumn<Products, String> productNameCol;
    @FXML
    private TableColumn<Products, String> priceCol;
    @FXML
    private TableColumn<Products, String> quantityAvailableCol;
    @FXML
    private TableColumn<Products, String> purchasedCol;
    @FXML
    private VBox farmersHBox;
    @FXML
    private TableView<Products> productsTable;
    @FXML
    private TableView<Orders> ordersTable;
    @FXML
    private TableColumn<Orders, String> customerName;
    @FXML
    private TableColumn<Orders, String> productNameOrderCol;
    @FXML
    private TableColumn<Orders, String> quantityOrderdCol;
    @FXML
    private TableColumn<Orders, String> totalCostCol;
    @FXML
    private TableColumn<Orders, String> statusCol;

    int farmerid;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        farmerid = database.DatabaseFunctions.getId(database.DatabaseFunctions.checkLoggedIn());
        initTableProducts();
        initTableOrders();
        loadDataProducts();
        loadDataOrders();
        productsTable.setVisible(true);
        ordersTable.setVisible(false);
    }

    private void initTableProducts() {
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityAvailableCol.setCellValueFactory(new PropertyValueFactory<>("quantityavailable"));
        purchasedCol.setCellValueFactory(new PropertyValueFactory<>("purchased"));
    }

    private void initTableOrders() {
        customerName.setCellValueFactory(new PropertyValueFactory<>("customername"));
        productNameOrderCol.setCellValueFactory(new PropertyValueFactory<>("productname"));
        quantityOrderdCol.setCellValueFactory(new PropertyValueFactory<>("quantityordered"));
        totalCostCol.setCellValueFactory(new PropertyValueFactory<>("totalcost"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadDataProducts() {
        listProducts.clear();

        String query = "SELECT * FROM products WHERE farmerid='" + farmerid + "'";
        ResultSet rs = database.DatabaseFunctions.execQuery(query);
        try {
            while (rs.next()) {
                listProducts.add(new Products(
                        rs.getString("productname"),
                        rs.getString("price"),
                        rs.getString("quantity"),
                        database.DatabaseFunctions.getPurchasedProductsByProductId(rs.getString("id"))
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        productsTable.getItems().setAll(listProducts);
    }

    private void loadDataOrders() {
    }

    @FXML
    private void logoutAction(ActionEvent event) {
        functions.functions.logout();
    }

    @FXML
    private void closeSystem(ActionEvent event) {
        functions.functions.CloseSystem();
    }

    @FXML
    private void goHomeAction(ActionEvent event) throws IOException {
        motherPane.getChildren().clear();
        motherPane.getChildren().add(FXMLLoader.load(getClass().getResource("/view/customerPage.fxml")));
    }

    @FXML
    private void userProfileAction(ActionEvent event) throws IOException {
        motherPane.getChildren().clear();
        motherPane.getChildren().add(FXMLLoader.load(getClass().getResource("/view/farmeruserpage.fxml")));

    }

    @FXML
    private void ordersAction(ActionEvent event) throws IOException {
        farmersHBox.getChildren().remove(productsTable);
        ordersTable.setVisible(true);
        farmersHBox.getChildren().set(1, ordersTable);
        ordersTable.setVisible(true);
    }

    @FXML
    private void searchProduct(KeyEvent event) {
        System.out.println("Searching");
    }

    @FXML
    private void addNewProduct(ActionEvent event) {
        Dialog<Pair<String, String>> newProduct = new Dialog();
        newProduct.setTitle("Add new Product | DIFA");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        newProduct.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(20, 150, 10, 10));

        Label productName = new Label("Name");
        JFXTextField productNameField = new JFXTextField();
        productNameField.setPromptText("Enter Product Name");

        Label userName = new Label("Username");
        JFXTextField userNameField = new JFXTextField();
        userNameField.setPromptText("Enter your username");
        Label password = new Label("Password");
        JFXPasswordField passwordField = new JFXPasswordField();
        userNameField.setPromptText("Enter your password");

        Label price = new Label("Price Per Unit");
        JFXTextField priceField = new JFXTextField();
        priceField.setPromptText("Enter Price per Unit");

        Label quantity = new Label("Quantity");
        JFXTextField quantityField = new JFXTextField();
        quantityField.setPromptText("Quantity");

        gp.add(productName, 0, 0);
        gp.add(productNameField, 1, 0);
        gp.add(price, 0, 1);
        gp.add(priceField, 1, 1);
        gp.add(quantity, 0, 2);
        gp.add(quantityField, 1, 2);
        gp.add(userName, 0, 3);
        gp.add(userNameField, 1, 3);
        gp.add(password, 0, 4);
        gp.add(passwordField, 1, 4);

        newProduct.getDialogPane().setContent(gp);

        newProduct.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                if (login(userNameField.getText(), passwordField.getText())) {
                    if (database.DatabaseFunctions.addNewProduct(productNameField.getText().toUpperCase(), priceField.getText().toUpperCase(), quantityField.getText(), userNameField.getText())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setContentText("New Product Added Successfully");
                        alert.setHeaderText("Success!");
                        alert.showAndWait();
                    } else {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setContentText("Some Error Occurred");
                        alert2.setHeaderText("Error!");
                        alert2.showAndWait();
                    }
                }
            }
            return null;
        });

        Optional<Pair<String, String>> resut = newProduct.showAndWait();

        resut.ifPresent(pair -> {
            System.out.println("Later Please");
        });

    }

    public static class Products {

        private final SimpleStringProperty name;
        private final SimpleStringProperty price;
        private final SimpleStringProperty quantityavailable;
        private final SimpleStringProperty quantitypurchased;

        public Products(String productname, String price, String quantityavailable, String quantitypurchased) {
            this.name = new SimpleStringProperty(productname);
            this.price = new SimpleStringProperty(price);
            this.quantityavailable = new SimpleStringProperty(quantityavailable);
            this.quantitypurchased = new SimpleStringProperty(quantitypurchased);
        }

        public String getProductname() {
            return name.get();
        }

        public String getPrice() {
            return price.get();
        }

        public String getQuantityavailable() {
            return quantityavailable.get();
        }

        public String getQuantitypurchased() {
            return quantitypurchased.get();
        }
    }

    public static class Orders {

        private final SimpleStringProperty customername;
        private final SimpleStringProperty productname;
        private final SimpleStringProperty quantitypurchased;
        private final SimpleStringProperty totalcost;
        private final SimpleStringProperty status;

        public Orders(String productname, String customername, String quantitypurchased, String totalcost, String status) {
            this.productname = new SimpleStringProperty(productname);
            this.customername = new SimpleStringProperty(customername);
            this.totalcost = new SimpleStringProperty(totalcost);
            this.quantitypurchased = new SimpleStringProperty(quantitypurchased);
            this.status = new SimpleStringProperty(status);
        }

        public String getProductname() {
            return productname.get();
        }

        public String getCustomername() {
            return customername.get();
        }

        public String getQuantitypurchased() {
            return quantitypurchased.get();
        }

        public String getStatus() {
            return status.get();
        }
        public String getTotalcost() {
            return totalcost.get();
        }
    }
}
