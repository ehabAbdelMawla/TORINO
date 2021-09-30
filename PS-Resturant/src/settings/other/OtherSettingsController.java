package settings.other;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToggleButton;
import controlpanel.ControlPanel;
import datamodel.settings.Preferences;
import datamodel.settings.PreferencesType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import util.Logger;
import util.gui.HelperMethods;
import util.gui.load.DialogHelper;

public class OtherSettingsController implements Initializable {

    private JFXDialog dialog;
    @FXML
    private JFXToggleButton printOption, cashDrawerwithoutSaleOption, cashDrawerwithSaleOption, backupOnEndShiftOption;
    @FXML
    private JFXCheckBox print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery;
    @FXML
    private FlowPane printCheckBoxContainer;

    public void setData(JFXDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setToggleButtonValue(printOption, Preferences.getInstance().getBoolean(PreferencesType.Print, "true"));
            if (printOption.isSelected()) {
                HelperMethods.setDisableCheckBoxs(false, print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery);
                print_playingRooms.setSelected(Preferences.getInstance().getBoolean(PreferencesType.Print_PlayingRooms, "true"));
                print_EndDay.setSelected(Preferences.getInstance().getBoolean(PreferencesType.Print_EndDay, "true"));
                print_RestaurantHall.setSelected(Preferences.getInstance().getBoolean(PreferencesType.Print_RestaurantHall, "true"));
                print_TakeAway.setSelected(Preferences.getInstance().getBoolean(PreferencesType.Print_TakeAway, "true"));
                print_Bar.setSelected(Preferences.getInstance().getBoolean(PreferencesType.Print_Bar, "true"));
                print_Delivery.setSelected(Preferences.getInstance().getBoolean(PreferencesType.Print_Delivery, "true"));
            } else {
                HelperMethods.unSelectCheckBoxs(print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery);
                HelperMethods.setDisableCheckBoxs(true, print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery);
            }
            setToggleButtonValue(cashDrawerwithSaleOption, Preferences.getInstance().getBoolean(PreferencesType.OpenCashDrawerWithSale, "true"));
            setToggleButtonValue(cashDrawerwithoutSaleOption, Preferences.getInstance().getBoolean(PreferencesType.OpenCashDrawerWithoutSale, "true"));
            setToggleButtonValue(backupOnEndShiftOption, Preferences.getInstance().getBoolean(PreferencesType.BackupOnEndShift, "true"));
            // Remove unneeded options
            if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
                printCheckBoxContainer.getChildren().remove(print_playingRooms);
            }
            if(!ControlPanel.getInstance().HAS_RESTAURANT){
                printCheckBoxContainer.getChildren().remove(print_TakeAway);
            }
            if (!ControlPanel.getInstance().HAS_RESTAURANT || !ControlPanel.getInstance().ACCESS_HALL_TABLES) {
                printCheckBoxContainer.getChildren().remove(print_RestaurantHall);
            }
            if (!ControlPanel.getInstance().HAS_RESTAURANT || !ControlPanel.getInstance().RESTAURANT_BAR) {
                printCheckBoxContainer.getChildren().remove(print_Bar);
            }
            if (!ControlPanel.getInstance().HAS_RESTAURANT || !ControlPanel.getInstance().RESTAURANT_Delivery) {
                printCheckBoxContainer.getChildren().remove(print_Delivery);
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initialize() :-" + ex);
        }
    }

    private void setToggleButtonValue(JFXToggleButton toggleButton, boolean isSelected) {
        toggleButton.setSelected(isSelected);
        toggleButton.setText(isSelected ? "مُفعلة" : "مُعطلة");
    }

    @FXML
    private void closeAction() {
        dialog.close();
    }

    @FXML
    private void saveAction() {
        try {
            if (DialogHelper.getInstance().showConfirmAlert("لكي يتم تطبيق التغييرات يجب إعادة تشغيل البرنامج." + "\n" + "إغلاق البرنامج؟") == 1) {
                Preferences.getInstance().set(PreferencesType.Print, String.valueOf(printOption.isSelected()));
                Preferences.getInstance().set(PreferencesType.OpenCashDrawerWithSale, String.valueOf(cashDrawerwithSaleOption.isSelected()));
                Preferences.getInstance().set(PreferencesType.OpenCashDrawerWithoutSale, String.valueOf(cashDrawerwithoutSaleOption.isSelected()));
                Preferences.getInstance().set(PreferencesType.BackupOnEndShift, String.valueOf(backupOnEndShiftOption.isSelected()));

                Preferences.getInstance().set(PreferencesType.Print_PlayingRooms, String.valueOf(print_playingRooms.isSelected()));
                Preferences.getInstance().set(PreferencesType.Print_EndDay, String.valueOf(print_EndDay.isSelected()));
                Preferences.getInstance().set(PreferencesType.Print_RestaurantHall, String.valueOf(print_RestaurantHall.isSelected()));
                Preferences.getInstance().set(PreferencesType.Print_TakeAway, String.valueOf(print_TakeAway.isSelected()));
                Preferences.getInstance().set(PreferencesType.Print_Delivery, String.valueOf(print_Delivery.isSelected()));
                Preferences.getInstance().set(PreferencesType.Print_Bar, String.valueOf(print_Bar.isSelected()));
                Platform.exit();
                System.exit(0);
            } else {
                System.out.println("Did not save Preferences");
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".saveAction() :-" + ex);
        }
        closeAction();
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            saveAction();
        }
    }

    @FXML
    private void toggleAction(ActionEvent event) {
        JFXToggleButton toggleButton = ((JFXToggleButton) event.getSource());
        if (toggleButton.isSelected()) {
            toggleButton.setText("مُفعلة");
        } else {
            toggleButton.setText("مُعطلة");
        }
    }

    @FXML
    private void printOptionOnAction(ActionEvent event) {
        toggleAction(event);
        if (!printOption.isSelected()) {
            HelperMethods.unSelectCheckBoxs(print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery);
            HelperMethods.setDisableCheckBoxs(true, print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery);
        } else {
            HelperMethods.setDisableCheckBoxs(false, print_playingRooms, print_EndDay, print_RestaurantHall, print_TakeAway, print_Bar, print_Delivery);
        }
    }
}
