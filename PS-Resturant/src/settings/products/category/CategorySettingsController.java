package settings.products.category;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import settings.products.AddCategoryController;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.button.CategoryButton;
import util.gui.load.DialogHelper;

public class CategorySettingsController implements Initializable {

    @FXML
    private Text CategroyLabel;
    @FXML
    private FlowPane categoryFlowPane;
    public CategoryButton FocusedButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void initCategoryButtons() {
        categoryFlowPane.getChildren().clear();
        ObservableList<CategoryButton> listOFCategoryButton = FXCollections.observableArrayList();
        for (String category : CategoryButton.ALL_CATEGORIES) {
            CategoryButton categoryButton = new CategoryButton(category);
            categoryButton.setOnAction(openCategory(categoryButton));
            listOFCategoryButton.add(categoryButton);
        }
        if (!listOFCategoryButton.isEmpty()) {
            FocusedButton = listOFCategoryButton.get(0);
            FocusedButton.setFocus();
            CategroyLabel.setText(FocusedButton.getText());
            util.gui.load.LoadHelper.getInstance().productSettingsController.setProductsOfSelectedCategory(FocusedButton.getText());
            FocusedButton.requestFocus();
        } else {
            CategroyLabel.setText("");
            util.gui.load.LoadHelper.getInstance().productSettingsController.setProductsOfSelectedCategory("");
        }
        categoryFlowPane.getChildren().setAll(listOFCategoryButton);
    }

    private EventHandler<ActionEvent> openCategory(CategoryButton b) {
        return (ActionEvent e) -> {
            try {
                FocusedButton.delFocus();
                b.setFocus();
                FocusedButton = b;
                CategroyLabel.setText(b.getText());
                util.gui.load.LoadHelper.getInstance().productSettingsController.setProductsOfSelectedCategory(FocusedButton.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.writeLog("Exception in " + getClass().getName() + ".openCategory() :- " + ex);
            }
        };
    }

    @FXML
    private void editCategory() {
        try {
            if (!CategoryButton.ALL_CATEGORIES.isEmpty()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings/products/AddCategory.fxml"));
                Scene scene = new Scene(loader.load());
                AddCategoryController controller = loader.getController();
                Stage stage = util.gui.BuilderUI.initStageDecorated(scene, "تعديل", "info");
                controller.setData(false, FocusedButton.getText());
                stage.showAndWait();
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".editCategory() :- " + ex);
        }
    }

    @FXML
    private void AddCategory() {
        try {
            if (DatabaseHandler.canAddMoreRowsInTable("cafecategories")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/settings/products/AddCategory.fxml"));
                Scene scene = new Scene(loader.load());
                AddCategoryController controller = loader.getController();
                Stage stage = util.gui.BuilderUI.initStageDecorated(scene, "إضافة", "info");
                controller.setData(true, "");
                stage.showAndWait();
            } else {
                DialogHelper.getInstance().showOKAlert("لا يمكن اضافة المزيد في النسخة التجريبية");
            }

        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".AddCategory() :- " + ex);
        }
    }

    @FXML
    private void delCategory() {
        if (!CategoryButton.ALL_CATEGORIES.isEmpty() && DialogHelper.getInstance().showConfirmAlert("حذف الفئة؟") == 1) {
            try {
                int x = util.db.Methods.abilityForDelete("SELECT categ FROM room1drink WHERE categ='" + FocusedButton.getText() + "'");
                x += util.db.Methods.abilityForDelete("SELECT productCateg FROM dailycafesheet WHERE productCateg='" + FocusedButton.getText() + "'");
                if (x != 0) {
                    DialogHelper.getInstance().showOKAlert("لا يمكن حذف هذه الفئة حاليا");
                    return;
                }
                if (DialogHelper.getInstance().showConfirmAlert("سوف يتم حذف الفئة و منتاجتها؟") == 1) {
                    String s = "DELETE FROM cafecategories WHERE categName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(s);
                    DatabaseHandler.stat.setString(1, FocusedButton.getText());
                    DatabaseHandler.stat.execute();
                    AddCategoryController.anyActNotation = 1;
                    // reload category
                    CategoryButton.getAllCategories();
                    initCategoryButtons();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + " -> delCategory :- " + e);
            }
        }
    }

}
