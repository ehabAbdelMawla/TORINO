package restaurant;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import datamodel.Order;
import dialog.checkout.CheckOutBillController;
import homepage.HomePageController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import restaurant.takeaway.TakeAwayController;
import util.Logger;
import util.gui.load.LoadHelper;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Screen;
import util.db.DatabaseHandler;
import util.db.Methods;

public class RestaurantHomeController implements Initializable {

    public static int anyAct = 0;

    @FXML
    private ScrollPane SP;
    @FXML
    private JFXButton TakeAwayBtn;
    @FXML
    private JFXButton BarBtn;
    @FXML
    private JFXButton HallBtn;
    private static JFXButton SelectedButton;
    @FXML
    private FlowPane flowPane;
    @FXML
    private JFXButton dailyDetailsBTN;
    @FXML
    private JFXButton deliveryBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Tooltip tooltip = new Tooltip("Open Daily Details - " + "فتح تفاصيل اليوم");
        Tooltip.install(dailyDetailsBTN, tooltip);
        tooltip.getStyleClass().add("tooltip-b");
        if (!ControlPanel.getInstance().ACCESS_HALL_TABLES) {
            flowPane.getChildren().remove(HallBtn);
        }
        if (!ControlPanel.getInstance().RESTAURANT_BAR) {
            flowPane.getChildren().remove(BarBtn);
        }
        if (!ControlPanel.getInstance().RESTAURANT_Delivery) {
            flowPane.getChildren().remove(deliveryBtn);
        }
    }

    @FXML
    private void goToDelivery() {
        goToTakeAwayOrder(Order.OrderKind.Delivery);
    }

    @FXML
    private void goToTakeAway() {
        goToTakeAwayOrder(Order.OrderKind.TakeAway);
    }

    @FXML
    private void goToBar() {
        goToTakeAwayOrder(Order.OrderKind.Bar);
    }

    private void goToTakeAwayOrder(Order.OrderKind orderKind) {
        try {
            // config data
            TakeAwayController.orderKind = orderKind;
            TakeAwayController.customerRadioBtnPointer.setSelected(true);
            // Reset List
            LoadHelper.getInstance().takeAwayController.resetOrderList();
            CheckOutBillController.setOrderKind(orderKind);
            // show stage
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            // TODO make stage open with relative size
            LoadHelper.getInstance().Stage_TakeAway.setX(bounds.getWidth() / 2 - (LoadHelper.getInstance().takeAwayController.AP.getPrefWidth() / 2));
            LoadHelper.getInstance().Stage_TakeAway.setY(bounds.getHeight() / 2 - (LoadHelper.getInstance().takeAwayController.AP.getPrefHeight() / 2));
            LoadHelper.getInstance().Stage_TakeAway.setMaximized(true);
            LoadHelper.getInstance().Stage_TakeAway.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".goToTakeAwayOrder(" + orderKind + ") :-" + e);
        }
    }

    @FXML
    private void goToHall() {
        HomePageController.borderPane_Static.setCenter(LoadHelper.getInstance().screenMap.get("root_Hall"));
        HomePageController.title_Label.setText(HallBtn.getText());
    }

    private void focusButton(JFXButton b) {
        SelectedButton.getStyleClass().remove("cashier-buttons-focused");
        b.getStyleClass().add("cashier-buttons-focused");
        SelectedButton = b;
    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, SP);
    }

    @FXML
    private void goToDailyDetails() {
        toolbar.ToolbarController.showDailyDetails("");
    }

    // TODO delete this method if not used
    public static void insertaNoteForCafe(String src, String note) {
        try {
            String sql = "INSERT INTO cafeandexpnotes VALUES(?,?,?,?)";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setInt(1, (int) Methods.GetMaximum("id", "cafeandexpnotes"));
            DatabaseHandler.stat.setString(2, Methods.getMyDay());
            DatabaseHandler.stat.setString(3, src);
            DatabaseHandler.stat.setString(4, note);
            DatabaseHandler.stat.execute();
        } catch (Exception e) {
            Logger.writeLog("Exception in insertaNoteForCafe");
        }
    }
}
