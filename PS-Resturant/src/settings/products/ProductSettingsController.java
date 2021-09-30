package settings.products;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controlpanel.ControlPanel;
import datamodel.DrinkPriceTableRecord;
import datamodel.Product;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import settings.products.category.CategorySettingsController;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.BuilderUI;
import util.gui.HelperMethods;
import util.gui.button.CategoryButton;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;

public class ProductSettingsController implements Initializable {

    public CategorySettingsController categorySettingsController;
    private ObservableList<Product> SELECETED_PRODUCTS;
    private Stage stageNewProduct;
    // ====== GUI OBJECTS =====
    @FXML
    private TextField searchComBox;
    @FXML
    private JFXTreeTableView<Product> productsTable;
    @FXML
    private TreeTableColumn<Product, String> productNameCol;
    @FXML
    private TreeTableColumn<Product, Double> guestPriceCol;
    @FXML
    private TreeTableColumn<Product, Double> staffPriceCol;
    @FXML
    private TreeTableColumn<Product, TableViewButton> editCol;
    @FXML
    private TreeTableColumn<Product, TableViewButton> delCol;
    @FXML
    private HBox parentBox;

    public void setData(CategorySettingsController controller, Parent root) {
        this.categorySettingsController = controller;
        categorySettingsController.initCategoryButtons();
        parentBox.getChildren().add(0, root);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            configureProductsTable();
            if (ControlPanel.getInstance().BARCODE_OPTION) {
                searchComBox.textProperty().addListener((observable, oldValue, newValue) -> productsTable.setPredicate(orderTreeItem
                        -> orderTreeItem.getValue().getName().toLowerCase().contains(searchComBox.getText().toLowerCase().trim())
                        || (orderTreeItem.getValue().haveBarCode()
                        && orderTreeItem.getValue().getBarCode().toLowerCase().contains(searchComBox.getText().toLowerCase().trim()))
                ));
            } else {
                searchComBox.setPromptText("بحث باسم المنتج");
                searchComBox.textProperty().addListener((observable, oldValue, newValue) -> productsTable.setPredicate(orderTreeItem
                        -> orderTreeItem.getValue().getName().contains(searchComBox.getText().trim())));
            }
            Parent root = FXMLLoader.load(getClass().getResource("/settings/products/NewProduct.fxml"));
            Scene sc = new Scene(root);
            Platform.runLater(() -> {
                stageNewProduct = BuilderUI.initStageDecorated(sc, "New Product", "info");
            });
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".initialize() :- " + e);
        }
    }

    private void configureProductsTable() {
        productNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        guestPriceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("guestPrice"));
        staffPriceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("staffPrice"));
        editCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("edit"));
        delCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("del"));
        HelperMethods.TableColumnAlignment(productNameCol, guestPriceCol, staffPriceCol, editCol, delCol);
        SELECETED_PRODUCTS = FXCollections.observableArrayList();
        TreeItem<Product> root = new RecursiveTreeItem<>(SELECETED_PRODUCTS, RecursiveTreeObject::getChildren);
        productsTable.setRoot(root);
        productsTable.setShowRoot(false);
    }

    @FXML
    private void newProduct() {
        if (CategoryButton.ALL_CATEGORIES.isEmpty()) {
            return;
        }
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("drinkprice")) {
                NewProductController.productToEdit = null;
                NewProductController.fakeInitialize();
                stageNewProduct.showAndWait();
                stageNewProduct.setOnCloseRequest((ev) -> {
                    NewProductController.productToEdit = null;
                });
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> newProduct():- " + e);
            e.printStackTrace();
        }
    }

    public void setProductsOfSelectedCategory(String categoryName) {
        SELECETED_PRODUCTS.clear();
        try {
            String sqlState = "SELECT * FROM drinkprice where category=? ORDER BY id ASC";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, categoryName);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            while (DatabaseHandler.result.next()) {
                Product product = new Product(DatabaseHandler.result.getInt(3), DatabaseHandler.result.getString(1), DatabaseHandler.result.getString(5), DatabaseHandler.result.getDouble(2), DatabaseHandler.result.getDouble(4), DatabaseHandler.result.getString(6), DatabaseHandler.result.getString(7));
                product.getDel().setOnAction((event) -> {
                    deleteProductAction(product);
                });
                product.getEdit().setOnAction((event) -> {
                    editProductAction(product);
                });
                SELECETED_PRODUCTS.add(product);
            }
            DatabaseHandler.result.close();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> setProductsOfSpecificCategory :- " + e);
        }
    }

    private void editProductAction(Product product) {
        try {
            NewProductController.productToEdit = new DrinkPriceTableRecord(product.getId(), product.getName(), (float) product.getGuestPrice(), (float) product.getStaffPrice(), product.getCategory(), product.getArguments(), product.getBarCode());
            NewProductController.fakeInitialize();
            stageNewProduct.showAndWait();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> editProductAction :-" + e);
        }
    }

    private void deleteProductAction(Product product) {
        try {
            int x = util.db.Methods.abilityForDelete("SELECT drinkName FROM room1drink WHERE drinkName='" + product.getName() + "'");
            x += util.db.Methods.abilityForDelete("SELECT productCateg FROM dailycafesheet WHERE drinkName='" + product.getName() + "'");
            if (x != 0) {
                DialogHelper.getInstance().showOKAlert("لا يمكن حذف هذا المنتج حاليا");
                return;
            }
            if (DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من حذف المنتج؟") == 1) {
                AddCategoryController.anyActNotation = 1;
                String str = "DELETE FROM drinkprice WHERE drinkName=?";
                DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(str);
                DatabaseHandler.stat.setString(1, product.getName());
                DatabaseHandler.stat.execute();
                setProductsOfSelectedCategory(categorySettingsController.FocusedButton.getText());
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> deleteProductAction :- " + e);
        }
    }

    @FXML
    private void goToSettings() {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Settings"));
            homepage.HomePageController.title_Label.setText("الإعدادات");
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> goToSettings():- " + e);
        }
    }
}
