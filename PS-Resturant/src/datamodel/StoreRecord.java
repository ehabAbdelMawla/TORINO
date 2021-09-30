package datamodel;

import dialog.edit.ingredient.EditIngredientController;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.HelperMethods;
import util.gui.button.TableViewButton;
import util.gui.load.DialogHelper;
import util.gui.load.LoadHelper;
import util.gui.load.Location;

public class StoreRecord extends RecursiveTreeObject<StoreRecord> {

    public String productName;
    public float Count;
    public TableViewButton editBtn = new TableViewButton("", new FontAwesomeIconView(FontAwesomeIcon.EDIT));
    public TableViewButton delBtn = new TableViewButton("", new FontAwesomeIconView(FontAwesomeIcon.TRASH));

    public StoreRecord(String productName, float Count) {
        this.productName = productName;
        this.Count = HelperMethods.formatNum(Count);
        editBtn.setOnAction(this::editAction);
        delBtn.setOnAction(this::deleteAction);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getCount() {
        return Count;
    }

    public void setCount(float Count) {
        this.Count = HelperMethods.formatNum(Count);
    }

    public TableViewButton getDelBtn() {
        return delBtn;
    }

    public void setDelBtn(TableViewButton delBtn) {
        this.delBtn = delBtn;
    }

//    Events
    public void editAction(Event event) {
        try {
            if (showEditDialog() == 1) {
                LoadHelper.getInstance().storageController.fillCurrentStoreTable("");
                LoadHelper.getInstance().storageController.fillEnterStoreTable("storeenter");
                LoadHelper.getInstance().storageController.fillExitStoreTable("storeexit");
                LoadHelper.getInstance().storageController.fillt4("");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> DeleteAction:- " + e);
        }
    }

    private int showEditDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("EDIT_INGERDIENTS")));
            Scene scene = new Scene(fxmlLoader.load());
            EditIngredientController controller = fxmlLoader.getController();
            controller.initData(this);
            Stage stage = util.gui.BuilderUI.initStageUnDecorated(scene, "Edit", "info");
            stage.showAndWait();
            return controller.editNotation;
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".showEditDialog() : " + e);
            return 0;
        }
    }

    public void deleteAction(Event event) {
        try {
            if (DialogHelper.getInstance().showConfirmAlert("هل انت متأكد من الحذف؟") == 1) {
                DatabaseHandler.con.prepareStatement("DELETE FROM store WHERE product='" + this.productName + "'").execute();
                LoadHelper.getInstance().storageController.insertIntoEnterStoreTable(new storemoves(
                        LocalDate.now().toString(),
                        LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")),
                        User.CurrentUser.userName,
                        this.productName,
                        this.Count
                ), "storeexit");
                LoadHelper.getInstance().storageController.fillCurrentStoreTable("");
                LoadHelper.getInstance().storageController.fillExitStoreTable("storeexit");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> DeleteAction:- " + e);
        }
    }

    public TableViewButton getEditBtn() {
        return editBtn;
    }

    public void setEditBtn(TableViewButton editBtn) {
        this.editBtn = editBtn;
    }

}
