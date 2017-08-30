package com.conference.user;

import com.conference.Company;
import com.conference.DialogBox;
import com.conference.MySqlConnect;
import com.conference.Product;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;

import static com.conference.Conference.loginScene;
//import java.util.Date;

public class Retailer extends Visitor {
    private Connection cn = null;
    private PreparedStatement pst = null;
    private Statement st = null;
    private ResultSet rs = null;

    private Company company;
    private TableView<Product> productTable;
    private ObservableList<Product> products;
    private ComboBox<String> searchType;
    private Label companyId, companyName,  productId, productName, price, stock, search;
    private TextField productIdField, productNameField, priceField, stockField, searchField;
    private Button addProductButton, editProductButton, deleteProductButton, searchProductButton;

//    private companyId
    public Retailer(int userId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        super(userId, firstName, lastName, gender, contactNumber, address, dob, position);
    }


    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
//        MenuItem edit = new MenuItem("Edit");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));

        profile.getItems().addAll(logout);

        Menu manageProduct = new Menu("Products");
        MenuItem sellProduct = new MenuItem("Sell Products");
        MenuItem viewProduct = new MenuItem("View Products");
        viewProduct.setOnAction(e -> layout.setCenter(viewProductView()));

        manageProduct.getItems().addAll(sellProduct, viewProduct);


        menuBar.getMenus().addAll(profile, manageProduct);


        layout.setCenter(mainView());
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 800, 480);
        stage.setTitle("Login As : Retailer");
        stage.setScene(scene);
    }

    private GridPane mainView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        Label loginId = new Label("ID : " + getUserId());
        GridPane.setConstraints(loginId, 0, 0);

        Label loginName = new Label("Name : " + getFirstName() + " " + getLastName());
        GridPane.setConstraints(loginName, 0, 1);

        Label loginLevel = new Label("Login Level : " + getPosition());
        GridPane.setConstraints(loginLevel, 0, 2);

        try {
            cn = MySqlConnect.connectDB();
            String sql = "SELECT company.companyId, company.name FROM member,company,work WHERE member.memberId = work.memberId AND work.companyId = company.companyId AND member.memberId = " + getUserId();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            if(rs.next()) {
                companyId = new Label("Company ID : " + rs.getString(1));
                companyName = new Label("Company Name : " + rs.getString(2));
                company = new Company(rs.getString(1), rs.getString(2));
            } else {
                companyId = new Label("Company ID : ");
                companyName = new Label("Company Name : ");
            }
            GridPane.setConstraints(companyId, 0, 3);
            GridPane.setConstraints(companyName, 0, 4);
        } catch(Exception e) {
            DialogBox.alertBox("Warning", e + "");
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "rs");
            }
            try {
                if(st != null) {
                    st.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "cn");
            }
        }

        body.getChildren().addAll(loginId, loginName, loginLevel, companyId, companyName);

        return body;
    }

    private GridPane viewProductView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        productName = new Label("Product Name : ");
        GridPane.setConstraints(productName, 0,0);
        productNameField = new TextField();
        productNameField.setPromptText("Insert Product Name");
        GridPane.setConstraints(productNameField, 1, 0);

        price = new Label("Price : ");
        GridPane.setConstraints(price, 0, 1);
        priceField = new TextField();
        priceField.setPromptText("Insert Price");
        GridPane.setConstraints(priceField, 1, 1);

        stock = new Label("Stock : ");
        GridPane.setConstraints(stock, 0, 2);
        stockField = new TextField();
        stockField.setPromptText("Insert Stock");
        GridPane.setConstraints(stockField, 1, 2);

        productId = new Label("Product ID : ");
        GridPane.setConstraints(productId, 0, 3);
        productIdField = new TextField();
        productIdField.setPromptText("Insert Product ID");
        GridPane.setConstraints(productIdField, 1, 3);

        addProductButton = new Button("Add Product");
        GridPane.setConstraints(addProductButton, 1, 4);
        addProductButton.setOnAction(e -> addProduct());
        addProductButton.setPrefWidth(150);

        editProductButton = new Button("Edit Product");
        GridPane.setConstraints(editProductButton, 2, 4);
        editProductButton.setPrefWidth(150);
        editProductButton.setOnAction(e -> Platform.runLater(() ->productTable.getSelectionModel().clearSelection()));

        deleteProductButton = new Button("Delete Product");
        GridPane.setConstraints(deleteProductButton, 3, 4);
        deleteProductButton.setOnAction(e -> deleteProduct());
        deleteProductButton.setPrefWidth(150);

        HBox searchContainer = new HBox();
        searchContainer.setPadding(new Insets(10));
        searchContainer.setSpacing(10);

        search = new Label("Search : ");
        searchField = new TextField();
        searchField.setPromptText("Insert Something");
        searchField.setPrefWidth(200);

        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("ProductID", "Name");
//        searchType.setValue("Name");
        searchType.getSelectionModel().select(1);
        searchProductButton = new Button("Search");
        searchProductButton.setOnAction(e -> searchProduct());

        searchContainer.getChildren().addAll(search, searchField, searchType, searchProductButton);

        GridPane.setConstraints(searchContainer, 0, 5, 4, 1);

        TableColumn<Product, String> productIdColumn = new TableColumn<>("ID");
        productIdColumn.setMinWidth(200);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setMinWidth(200);
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setMinWidth(100);
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));


        productTable = new TableView<>();
        productTable.setItems(getProducts());
        productTable.getSelectionModel().selectedItemProperty().addListener((v, oldV, newV) -> {
            // if the newValue not null -> get the value and set to the field
                // if the newValue is null -> do nothing, dont get the value from the previous selected
                // otherwise it will give the null pointer exception
            if(newV != null) {
                productIdField.setText(productTable.getSelectionModel().getSelectedItem().getProductId());
                productNameField.setText(productTable.getSelectionModel().getSelectedItem().getName());
                priceField.setText(productTable.getSelectionModel().getSelectedItem().getPrice() + "");
                stockField.setText(productTable.getSelectionModel().getSelectedItem().getStock() + "");
            }
        });

        productTable.minWidth(480);
        productTable.getColumns().addAll(productIdColumn, productNameColumn, priceColumn, stockColumn);


        GridPane.setConstraints(productTable, 0, 6, 4, 1);
        body.getChildren().addAll(productName, price, stock, productId, addProductButton,
                editProductButton, deleteProductButton,
                searchContainer,
                productTable);
        body.getChildren().addAll(productNameField, priceField, stockField, productIdField);

//        body.getColumnConstraints().add(new ColumnConstraints(150));
//        body.getColumnConstraints().add(new ColumnConstraints(150));
//        body.getColumnConstraints().add(new ColumnConstraints(150));

        return body;
    }

    public void deleteProduct() {
        try {
            cn = MySqlConnect.connectDB();
            String sqlOwn = "DELETE FROM own WHERE productId = ? AND companyId = ?";
            pst = cn.prepareStatement(sqlOwn);
            pst.setString(1, productIdField.getText());
            pst.setString(2, company.getCompanyId());
            pst.executeUpdate();

            String sqlProduct = "DELETE FROM product WHERE productId = ?";
            pst = cn.prepareStatement(sqlProduct);
            pst.setString(1, productIdField.getText());
            pst.executeUpdate();

            // set the items on productTable view using getProducts method to get the current data from database
            productTable.setItems(getProducts());
            DialogBox.alertBox("Success", productNameField.getText() + " Deleted Successfully");
        } catch (Exception e) {
            DialogBox.alertBox("Warning", e + "");
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "rs");
            }
            try {
                if(st != null) {
                    st.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "cn");
            }
        }
    }

    public void searchProduct() {
        products = FXCollections.observableArrayList();
        try {
            cn = MySqlConnect.connectDB();
            String sql = "SELECT * FROM product, company, own WHERE product.productId = own.productId AND own.companyId = company.companyId AND company.companyId = '" + company.getCompanyId() + "' AND product.Name LIKE '%e%'";
            pst = cn.prepareStatement(sql);
//            pst.setString(1, "name");
//            pst.setString(2, "'%" + searchField.getText() + "%'");
            rs = pst.executeQuery();

//            int i = 0;
            while(rs.next()) {
                products.add(new Product(rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getInt(4)));
//                selectionBox.getItems().add(lectures.get(i).getLectureId() + " - " + lectures.get(i).getTitle());
//                i++;
            }
            productTable.setItems(products);
        } catch (Exception e) {
            DialogBox.alertBox("Warning", e + "");
        }
    }
    public void addProduct() {
        try {
            cn = MySqlConnect.connectDB();
            String sqlProduct = "INSERT INTO product VALUES(?, ?, ?, ?)";
            pst = cn.prepareStatement(sqlProduct);
            pst.setString(1, productIdField.getText());
            pst.setString(2, productNameField.getText());
            pst.setDouble(3, Double.parseDouble(priceField.getText()));
            pst.setInt(4, Integer.parseInt(stockField.getText()));
            pst.executeUpdate();

            String sqlOwn = "INSERT INTO own VALUES(?,?)";
            pst = cn.prepareStatement(sqlOwn);
            pst.setString(1, company.getCompanyId());
            pst.setString(2, productIdField.getText());
            pst.executeUpdate();
            productTable.setItems(getProducts());
            DialogBox.alertBox("Success", productNameField.getText() + " Inserted Successfuly");
        } catch (Exception e) {
            DialogBox.alertBox("Warning", e + "");
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "rs");
            }
            try {
                if(st != null) {
                    st.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "cn");
            }
        }
    }


    private ObservableList<Product> getProducts() {
        products = FXCollections.observableArrayList();
        try {
            cn = MySqlConnect.connectDB();
            String sql = "SELECT product.* FROM product, company, own WHERE product.productId = own.productId AND own.companyId = company.companyId AND company.companyId = '" + company.getCompanyId() + "'";
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while(rs.next()) {
                products.add(new Product(rs.getString(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getInt(4)));
            }
        } catch (Exception e) {
            DialogBox.alertBox("Warning", e + "");
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "rs");
            }
            try {
                if(st != null) {
                    st.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "cn");
            }
        }
        return products;
    }
}
