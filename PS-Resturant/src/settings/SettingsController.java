package settings;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import datamodel.Order;
import homepage.HomePageController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import settings.other.OtherSettingsController;
import settings.tax.TaxSettingsController;
import util.Logger;
import util.gui.load.LoadHelper;
import util.gui.load.Location;

public class SettingsController implements Initializable {

    @FXML
    private JFXButton DataSettings;
    public static JFXButton datSettingsButton;
    @FXML
    private ScrollPane sp;
    @FXML
    private FlowPane buttonsContainer;
    @FXML
    private JFXButton shopDataButton, promotionsButton, roomNamesButton, hallTablesButton, devicesButton, el3ohdaButton, otherSettingsButton, taxAndServicesButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datSettingsButton = DataSettings;

        // Remove unAllowed Buttons
        if (!ControlPanel.getInstance().SEND_DAILYSHEET_MAIL && !ControlPanel.getInstance().ACCESS_RECIEPT_DATA) {
            buttonsContainer.getChildren().remove(shopDataButton);
        }
//        if (!(SEND_DAILYSHEET_MAIL && ACCESS_RECIEPT_DATA)) {
//        }
        if (!ControlPanel.getInstance().HOUR_PROMOTIONS && !ControlPanel.getInstance().DEVICE_PROMOTIONS) {
            buttonsContainer.getChildren().remove(promotionsButton);
        }
        if (!ControlPanel.getInstance().ACCESS_ROOM_NAMES) {
            buttonsContainer.getChildren().remove(roomNamesButton);
        }
        if (!ControlPanel.getInstance().ACCESS_TABLES_NAMES) {
            buttonsContainer.getChildren().remove(hallTablesButton);
        }
        if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
            buttonsContainer.getChildren().remove(devicesButton);
        }
        if (ControlPanel.getInstance().EXPENSESS_FROM_DAILY_INCOME) {
            buttonsContainer.getChildren().remove(el3ohdaButton);
        }

        if (!ControlPanel.getInstance().HAS_PLAYSTATION && !ControlPanel.getInstance().ACCESS_HALL_TABLES && !ControlPanel.getInstance().RESTAURANT_BAR) {
            buttonsContainer.getChildren().remove(taxAndServicesButton);
        }
    }

    @FXML
    private void goToEmployeesSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_EmployeeSettings"));
            homepage.HomePageController.title_Label.setText("إعدادات ال" + ((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In" + getClass().getName() + "-> goToEmployeesSettings:-" + ex);
        }
    }

    @FXML
    private void goToCafeSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_CafeSettings"));
            homepage.HomePageController.title_Label.setText("إعدادات " + ((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In" + getClass().getName() + "-> goToCafeSettings:-" + ex);
        }
    }

    @FXML
    private void goToDevicesSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_DevicesSettings"));
            homepage.HomePageController.title_Label.setText("إعدادات ال" + ((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In" + getClass().getName() + "-> goToDevicesSettings :-" + ex);
        }
    }

    @FXML
    private void goToDataSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_DeleteData"));
            homepage.HomePageController.title_Label.setText(((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In" + getClass().getName() + "-> goToDataSettings :-" + ex);
        }
    }

    @FXML
    private void goToImprestSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_ImprestSettings"));
            homepage.HomePageController.title_Label.setText("إعدادات " + ((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In" + getClass().getName() + "-> goToImprestSettings:-" + ex);
        }
    }

    @FXML
    private void goToPlayingRoomsSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_RoomSettings"));
            homepage.HomePageController.title_Label.setText("إعدادات " + ((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> goToPlayingRoomsSettings():-" + ex);
        }
    }

    @FXML
    private void goToRestaurantHallSettings(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_RestaurantHallSettings"));
            homepage.HomePageController.title_Label.setText("إعدادات " + ((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> goToRestaurantHallSettings():-" + ex);
        }
    }

    @FXML
    private void goToTaxSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("TAX_SERVICE")));
            VBox pane = fxmlLoader.load();
            TaxSettingsController controller = fxmlLoader.getController();

            JFXDialog dialog = new JFXDialog(homepage.HomePageController.homepageStackPane, pane, JFXDialog.DialogTransition.TOP);
            controller.setData(Order.getTax("restaurant"), Order.getServant("restaurant"), Order.getTax("rooms"), Order.getServant("rooms"), dialog);
            HomePageController.currentDialog = dialog;
            dialog.show();
        } catch (IOException ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".goToTaxSettings() :-" + ex);
        }
    }

    @FXML
    private void goToPromotion() {
        try {
            if (ControlPanel.getInstance().HOUR_PROMOTIONS && ControlPanel.getInstance().DEVICE_PROMOTIONS) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("CHOOSE_PROMO_TYPE")));
                JFXDialog dialog = new JFXDialog(homepage.HomePageController.homepageStackPane, fxmlLoader.load(), JFXDialog.DialogTransition.TOP);
                ((settings.promotion.ChoosePromoTypeController) fxmlLoader.getController()).dialog = dialog;
                HomePageController.currentDialog = dialog;
                dialog.show();
            } else if (ControlPanel.getInstance().HOUR_PROMOTIONS) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("HOUR_PROMOTIONS")));
                JFXDialog jFXDialog = new JFXDialog(homepage.HomePageController.homepageStackPane, fxmlLoader.load(), JFXDialog.DialogTransition.TOP);
                ((settings.promotion.HourPromotionController) fxmlLoader.getController()).dialog = jFXDialog;
                HomePageController.currentDialog = jFXDialog;
                jFXDialog.show();
            } else if (ControlPanel.getInstance().DEVICE_PROMOTIONS) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("DEVICE_PROMOTIONS")));
                JFXDialog jFXDialog = new JFXDialog(homepage.HomePageController.homepageStackPane, fxmlLoader.load(), JFXDialog.DialogTransition.TOP);
                ((settings.promotion.device.DevicePromoController) fxmlLoader.getController()).dialog = jFXDialog;
                HomePageController.currentDialog = jFXDialog;
                jFXDialog.show();
            }
        } catch (IOException ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".goToPromotion() :-" + ex);
        }
    }

    @FXML
    private void goToShopData(Event event) {
        try {
            homepage.HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_ShopData"));
            homepage.HomePageController.title_Label.setText(((JFXButton) event.getSource()).getText());
        } catch (Exception ex) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> goToShopData() -> " + ex);
        }
    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, sp);
    }

    @FXML
    private void goToOtherSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("OTHER_SETTINGS")));
            VBox pane = fxmlLoader.load();
            OtherSettingsController controller = fxmlLoader.getController();

            JFXDialog dialog = new JFXDialog(homepage.HomePageController.homepageStackPane, pane, JFXDialog.DialogTransition.TOP);
            controller.setData(dialog);
            HomePageController.currentDialog = dialog;
            dialog.show();
        } catch (IOException ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".goToOtherSettings() :-" + ex);
        }
    }
}
