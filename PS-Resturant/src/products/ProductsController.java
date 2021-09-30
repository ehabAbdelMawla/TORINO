package products;

import datamodel.DrinkPriceTableRecord;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controlpanel.ControlPanel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.FlowPane;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.button.CategoryButton;

public class ProductsController implements Initializable {

    public static int addingNotation = 0;
    public static CategoryButton FocusedButton;
    @FXML
    private JFXTreeTableView<DrinkPriceTableRecord> myTable;
    @FXML
    private TreeTableColumn<DrinkPriceTableRecord, String> DrinkNameCol;
    @FXML
    private TreeTableColumn<DrinkPriceTableRecord, Float> priceCol;
    @FXML
    private TreeTableColumn<DrinkPriceTableRecord, Float> price2Col;
    @FXML
    private FlowPane btnsContainer;
    @FXML
    private TextField searchComBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addingNotation = 0;
        DrinkNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("DrinkName"));
        priceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Price"));
        price2Col.setCellValueFactory(new TreeItemPropertyValueFactory<>("Price2"));

        util.gui.HelperMethods.TableColumnAlignment(priceCol, price2Col, DrinkNameCol);
        initCategoryButtons();

        if (ControlPanel.getInstance().BARCODE_OPTION) {
            searchComBox.textProperty().addListener((observable, oldValue, newValue) -> myTable.setPredicate(orderTreeItem
                    -> orderTreeItem.getValue().getDrinkName().toLowerCase().contains(searchComBox.getText().toLowerCase().trim())
                    || (orderTreeItem.getValue().haveBarCode()
                    && orderTreeItem.getValue().getBarCode().toLowerCase().contains(searchComBox.getText().toLowerCase().trim()))
            ));
        } else {
            searchComBox.setPromptText("بحث باسم المنتج");
            searchComBox.textProperty().addListener((observable, oldValue, newValue) -> myTable.setPredicate(orderTreeItem
                    -> orderTreeItem.getValue().getDrinkName().contains(searchComBox.getText().trim())));
        }
    }

    public ObservableList<DrinkPriceTableRecord> selectDrinksData(String s) {
        ObservableList<DrinkPriceTableRecord> list = FXCollections.observableArrayList();
        try {
            String sqlState = "SELECT * FROM DrinkPrice WHERE category='" + s + "' ORDER BY id ASC";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            while (DatabaseHandler.result.next()) {
                list.add(new DrinkPriceTableRecord(DatabaseHandler.result.getInt(3),
                        DatabaseHandler.result.getString(1),
                        DatabaseHandler.result.getFloat(2),
                        DatabaseHandler.result.getFloat(4),
                        DatabaseHandler.result.getString(5),
                        null, DatabaseHandler.result.getString(7)));
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> selectDrinksData(String s: " + s + "):-" + e);
            return list;
        }
    }

    public void setProductsOfSelectedCategory(String s) {
        ObservableList<DrinkPriceTableRecord> sers = selectDrinksData(s);
        TreeItem<DrinkPriceTableRecord> root = new RecursiveTreeItem<>(sers, RecursiveTreeObject::getChildren);
        myTable.setRoot(root);
        myTable.setShowRoot(false);
    }

    public void initCategoryButtons() {
        btnsContainer.getChildren().clear();

        ObservableList<CategoryButton> listOFCategoryButton = FXCollections.observableArrayList();
        // FROM DB
        for (String category : CategoryButton.ALL_CATEGORIES) {
            CategoryButton categoryButton = new CategoryButton(category);
            categoryButton.setOnAction(openCategory(categoryButton));
            listOFCategoryButton.add(categoryButton);
        }
        if (!listOFCategoryButton.isEmpty()) {
            FocusedButton = listOFCategoryButton.get(0);
            FocusedButton.setFocus();
            setProductsOfSelectedCategory(FocusedButton.getText());
        }
        btnsContainer.getChildren().addAll(listOFCategoryButton);
    }

    private EventHandler<ActionEvent> openCategory(CategoryButton b) {
        return (ActionEvent e) -> {
            try {
                FocusedButton.delFocus();
                b.setFocus();
                FocusedButton = b;
                setProductsOfSelectedCategory(FocusedButton.getText());
                searchComBox.setText("");
            } catch (Exception ex) {
                Logger.writeLog("Exception in : " + getClass().getName() + ".openCategory() :- " + ex);
            }
        };
    }

}
