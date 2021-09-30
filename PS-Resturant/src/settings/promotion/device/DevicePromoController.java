package settings.promotion.device;

import com.jfoenix.controls.JFXDialog;
import controlpanel.ControlPanel;
import datamodel.DevicePromotion;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.ScrollEvent;
import playing.price.PlayingPriceController;
import util.Logger;
import util.db.DatabaseHandler;

public class DevicePromoController implements Initializable {

    public JFXDialog dialog;
    @FXML
    private Spinner<Integer> discountSpinner;
    @FXML
    private javafx.scene.layout.VBox devicesPane;
    @FXML
    private ScrollPane sp;
    ObservableList<DevicePromotion> allDevices = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        discountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100));
        discountSpinner.getValueFactory().setValue(Integer.parseInt(PlayingPriceController.devicePercentage.getValue().replaceAll("%", "").trim()));
        initDevices();
    }

    @FXML
    private void saveAction() {
        try {
            System.out.println("saveAction .. ");
            allDevices.stream().forEach(DevicePromotion::updateState);
            DatabaseHandler.con.prepareStatement("UPDATE devicePromotions SET percentage=" + discountSpinner.getValue()).execute();
            PlayingPriceController.devicePercentage.set(discountSpinner.getValue() + "%");
            dialog.close();
        } catch (SQLException ex) {
            Logger.writeLog("Exception In " + getClass().getName() + ".saveAction() -> " + ex);
        }

    }

    private void initDevices() {
        devicesPane.getChildren().clear();
        try {
            final ObservableList<Node> deviceBoxs = FXCollections.observableArrayList();
            getDevices(allDevices, deviceBoxs);
            devicesPane.getChildren().addAll(deviceBoxs);
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + ".initDevices() -> " + e);
        }
    }

    public void getDevices(ObservableList<DevicePromotion> devices, ObservableList<Node> UiBoxes) {
        try {
            String removeMatchDevice = ControlPanel.getInstance().MATCH_DEVICE ? "" : "WHERE id>0";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement("SELECT device,promotionState FROM playingprice " + removeMatchDevice + " ORDER BY id ASC").executeQuery();
            FXMLLoader fXMLLoader;
            while (DatabaseHandler.result.next()) {
                fXMLLoader = new FXMLLoader(getClass().getResource("/settings/promotion/device/DeviceBox.fxml"));
                UiBoxes.add(fXMLLoader.load());
                ((DeviceBoxController) fXMLLoader.getController()).setData((new DevicePromotion(DatabaseHandler.result.getString(1), DatabaseHandler.result.getInt(2) == 1)));
                devices.add((DeviceBoxController) fXMLLoader.getController());
            }
            DatabaseHandler.result.close();
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + ".getDevices() :- " + e);
        }

    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, sp);
    }

}
