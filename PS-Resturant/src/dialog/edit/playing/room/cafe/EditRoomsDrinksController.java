package dialog.edit.playing.room.cafe;

import datamodel.ArgumentsClass;
import com.jfoenix.controls.JFXComboBox;
import datamodel.DrinkTableRecord;
import datamodel.Product;
import datamodel.User;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import storage.StorageController;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

public class EditRoomsDrinksController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXComboBox<String> Drinks;
    @FXML
    private Spinner<Integer> num;
    SpinnerValueFactory<Integer> NumsValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
    public static DrinkTableRecord updated;
    public static int flag = 0;
    @FXML
    private TextField reason;
    @FXML
    private Label error;
    @FXML
    private JFXComboBox<String> categCombo;
    public static String note = "";
    DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("hh:mm a");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            note = "";
            flag = 0;
            num.setValueFactory(NumsValues);
            num.getValueFactory().setValue(updated.Num);
            Drinks.setItems(DatabaseHandler.selectDrinksPriceDeviceNames(""));
            Drinks.setValue(updated.DName);
            error.setVisible(false);
            categCombo.setItems(DatabaseHandler.selectCategotiesNames(""));
            categCombo.setValue(updated.categ);
            Drinks.setValue(updated.DName);
        } catch (Exception e) {
            Logger.writeLog("Exception in EditRoomsDrinksController -> initialize:-" + e);
        }
    }

    @FXML
    private void closeWindow(Event event) {
        error.setVisible(false);
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
    }

    @FXML
    private void fillDrinksNames(MouseEvent event) {
        Drinks.setItems(DatabaseHandler.selectDrinksPriceDeviceNames("WHERE category='" + categCombo.getValue() + "'"));
    }

    @FXML
    private void editAction(Event event) {
        if ("".equals(reason.getText().trim())) {
            error.setVisible(true);
            return;
        }
        try {
            if (Drinks.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("- يجب اختيار المشروب");
                return;
            }
            StorageController.pullFromORPushTOStore(updated.DName, updated.proarguments, updated.Num, "push");
            int n = num.getValue();
            String DN = Drinks.getValue();
            float b = DatabaseHandler.getDrinkPrice("عميل", categCombo.getValue(), DN);
            String argsString = StorageController.getArgsOfProduct(categCombo.getValue(), DN);
            ObservableList<ArgumentsClass> argList = Product.convertStringToListOfArgs(argsString);
            int x = StorageController.ableToBuy(DN, argList, n);
            if (x == -5) {
                StorageController.pullFromORPushTOStore(updated.DName, updated.proarguments, updated.Num, "pull");
                DialogHelper.getInstance().showOKAlert("-هذا العدد غير متوافر!");
                return;
            }
            String reeeson = reason.getText().trim();
            if (updated.Num != num.getValue() && updated.DName.equals(DN)) {
                note = "قام " + " " + User.CurrentUser.userName + " " + "بتعديل عدد منتج " + " " + updated.DName + " " + "من " + " " + updated.Num + " " + "الي " + " " + n + " " + "السبب : " + " " + reeeson + " " + " وقت التعديل : " + " " + LocalTime.now().format(dtf2) + " ";
            } else if (updated.Num == num.getValue() && !updated.DName.equals(DN)) {
                note = "قام " + " " + User.CurrentUser.userName + " " + "بتعديل اسم  منتج  من " + " " + updated.DName + " " + "الي  " + " " + DN + " " + "السبب : " + " " + reeeson + " " + " وقت التعديل : " + " " + LocalTime.now().format(dtf2) + " ";
            } else if (updated.Num != num.getValue() && !updated.DName.equals(DN)) {
                note = "قام " + " " + User.CurrentUser.userName + " " + "بتعديل اسم  منتج  من " + " " + updated.DName + " " + "الي  " + " " + DN + " " + "وتعديل العدد من " + " " + updated.Num + " " + "الي " + " " + n + " " + "السبب : " + " " + reeeson + " " + " وقت التعديل : " + " " + LocalTime.now().format(dtf2) + " ";
            }

            if (b > 0) {
                updated = new DrinkTableRecord(updated.id,
                        DN,
                        n,
                        (b * n), updated.RoomName, argsString, categCombo.getValue());

                StorageController.pullFromORPushTOStore(DN, argList, n, "pull");
                flag = 1;
            } else {
                DialogHelper.getInstance().showOKAlert("- يجب اختيار المشروب");
                StorageController.pullFromORPushTOStore(updated.DName, updated.proarguments, updated.Num, "pull");
                return;
            }
            closeWindow(event);
        } catch (Exception e) {
            Logger.writeLog("Exception in EditRoomsDrinksController -> editAction:-" + e);
        }
    }

    @FXML
    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editAction((Event) event);
        } else if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow(event);
        }
    }

    @FXML
    private void fillCategories(MouseEvent event) {
        Drinks.setValue(null);
    }

}
