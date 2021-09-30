package settings.products;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import datamodel.CategRecord;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;
import util.db.Methods;
import util.gui.button.CategoryButton;
import util.gui.load.DialogHelper;

public class AddCategoryController implements Initializable {

    public static int anyActNotation = 0;

    @FXML
    private JFXTextField CategoryName;
    @FXML
    private JFXButton ConfirmButton;

    private boolean isADD;
    private String tempCategoryName;

    public void setData(boolean isAdd, String categoryName) {
        this.isADD = isAdd;
        this.tempCategoryName = categoryName;
        CategoryName.setText(this.isADD ? "" : categoryName);
        ConfirmButton.setText(this.isADD ? "إضافة" : "تعديل");
    }

    public String getData() {
        return CategoryName.getText().trim();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        anyActNotation = 0;
        isADD = false;
    }

    @FXML
    private void discardAction(Event event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void ConfirmAction(Event event) {
        if (this.isADD) {
            if (addCategoryAction()) {
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
        } else {
            if (editCategoryAction()) {
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
        }

    }

    private boolean addCategoryAction() {
        try {
            if (CategoryName.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("يجب ادخال اسم الفئة");
                return false;
            } else if (CategoryName.getText().trim().contains("@") || CategoryName.getText().trim().contains("#")) {
                DialogHelper.getInstance().showOKAlert("لا يمكن ان تحتوي الفئة  علي @او #");
                return false;
            } else {
                if (insertIntoCategoryTable(new CategRecord((int) Methods.GetMaximum("id", "cafecategories"), CategoryName.getText().trim()))) {
                    anyActNotation = 1;
                    CategoryButton.getAllCategories();
                    util.gui.load.LoadHelper.getInstance().productSettingsController.categorySettingsController.initCategoryButtons();
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> addCategoryAction :- " + e);
        }
        return false;
    }

    public boolean insertIntoCategoryTable(CategRecord rec) {
        try {
            String sql = "INSERT INTO cafecategories VALUES (?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setInt(1, rec.id);
            DatabaseHandler.stat.setString(2, rec.categName);
            DatabaseHandler.stat.execute();
        } catch (org.sqlite.SQLiteException ex) {
            DialogHelper.getInstance().showOKAlert("هذه الفئة موجودة ب الفعل");
            return false;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> insertIntoCategoryTable :- " + e);
            return false;
        }
        return true;
    }

    private boolean editCategoryAction() {
        try {
            if (!(tempCategoryName.equals(CategoryName.getText().trim()))) {
                if (!(CategoryName.getText().trim().equals(""))) {
                    if (CategoryName.getText().trim().contains("@") || CategoryName.getText().trim().contains("#")) {
                        DialogHelper.getInstance().showOKAlert("لا يمكن ان تحتوي الفئة  علي @او #");
                        return false;
                    }
                    String sql = "UPDATE cafecategories SET categName=? where categName=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, CategoryName.getText().trim());
                    DatabaseHandler.stat.setString(2, tempCategoryName);
                    try {
                        DatabaseHandler.stat.execute();
                        AddCategoryController.anyActNotation = 1;
                        CategoryButton.getAllCategories();
                        util.gui.load.LoadHelper.getInstance().productSettingsController.categorySettingsController.initCategoryButtons();
                        return true;
                    } catch (org.sqlite.SQLiteException e) {
                        DialogHelper.getInstance().showOKAlert("هذا الاسم موجود بالفعل!");
                        return false;
                    }
                } else {
                    DialogHelper.getInstance().showOKAlert("يجب ادخال اسم الفئة");
                }
            }
            return true;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> editCategoryAction:- " + e);
        }

        return false;
    }

    @FXML
    private void keyEV(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            ConfirmAction(event);
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            discardAction(event);
        }
    }

}
