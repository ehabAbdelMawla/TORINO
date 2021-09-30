/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package settings.tax;

import controlpanel.ControlPanel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.gui.load.DialogHelper;
import util.gui.validation.ValidationMethods;

public class TaxSettingsController implements Initializable {

    @FXML
    private JFXTextField service_TextField;
    @FXML
    private JFXTextField tax_TextField;
    @FXML
    private JFXButton restaurantBTN;
    @FXML
    private JFXButton playingRoomsBTN;
    @FXML
    private JFXTextField service_TextField_PlayingRooms;
    @FXML
    private JFXTextField tax_TextField_PlayingRooms;
    @FXML
    private VBox restaurantBox;
    @FXML
    private VBox playingRoomsBox;
    private JFXButton currentBTN;
    private JFXDialog dialog;
    @FXML
    private HBox buttonsContainer;

    public void setData(double taxRestaurant, double serviceRestaurant, double taxPlayingRooms, double servicePlayingRooms, JFXDialog dialog) {
        tax_TextField.setText(String.valueOf(taxRestaurant));
        service_TextField.setText(String.valueOf(serviceRestaurant));
        tax_TextField_PlayingRooms.setText(String.valueOf(taxPlayingRooms));
        service_TextField_PlayingRooms.setText(String.valueOf(servicePlayingRooms));
        this.dialog = dialog;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentBTN = ControlPanel.getInstance().HAS_PLAYSTATION ? playingRoomsBTN : restaurantBTN;
        if (!ControlPanel.getInstance().HAS_PLAYSTATION) {
            buttonsContainer.getChildren().remove(playingRoomsBTN);
        }
        
        if (!ControlPanel.getInstance().ACCESS_HALL_TABLES && !ControlPanel.getInstance().RESTAURANT_BAR) {
            buttonsContainer.getChildren().remove(restaurantBTN);
        }
        currentBTN.fire();
    }

    @FXML
    private void closeAction(Event event) {
        dialog.close();
    }

    @FXML
    private void saveAction(Event event) {
        try {
            if (!ValidationMethods.checkTextFeildConstraint(service_TextField, tax_TextField, service_TextField_PlayingRooms, tax_TextField_PlayingRooms)) {
                DialogHelper.getInstance().showOKAlert("يجب ادخال قيمة الخدمة والضريبة");
                return;
            }

            final double resturantTax, resturantService, roomsTax, roomsService;
            try {
                resturantTax = Double.parseDouble(tax_TextField.getText().trim());
                resturantService = Double.parseDouble(service_TextField.getText().trim());
                roomsTax = Double.parseDouble(tax_TextField_PlayingRooms.getText().trim());
                roomsService = Double.parseDouble(service_TextField_PlayingRooms.getText().trim());
                if (resturantTax < 0 || resturantService < 0 || roomsTax < 0 || roomsService < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                DialogHelper.getInstance().showOKAlert("القيم التي ادخلتها غير صحيحة");
                return;
            }

            updateTaxAndService(resturantTax, resturantService, "restaurant");
            updateTaxAndService(roomsTax, roomsService, "rooms");

        } catch (Exception e) {
            Logger.writeLog("Exception in TaxSettingsController -> saveAction() " + e);
        }
        closeAction(event);
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            saveAction(event);
        }
    }

    @FXML
    private void goToRestaurantData(ActionEvent event) {
        toggleBTN(restaurantBTN);
        restaurantBox.setVisible(true);
        playingRoomsBox.setVisible(false);
    }

    @FXML
    private void goToPlayingRoomsData(ActionEvent event) {
        toggleBTN(playingRoomsBTN);
        playingRoomsBox.setVisible(true);
        restaurantBox.setVisible(false);
    }

    private void toggleBTN(JFXButton b) {
        currentBTN.getStyleClass().remove("dark-button");
        currentBTN = b;
        currentBTN.getStyleClass().add("dark-button");
    }

    private void updateTaxAndService(double tax, double servant, String dbFlag) {
        DBCRUD temp = new DBCRUD("taxAndServ") {
            @Override
            public ArrayList createMap() {
                ArrayList<DBField> arr = new ArrayList<>();
                arr.add(new DBField(dbFlag, "target", "PK"));
                arr.add(new DBField(servant, "serv", "NN"));
                arr.add(new DBField(tax, "tax", "NN"));
                return arr;
            }
        };
        temp.setFields(temp.createMap());
        temp.update();
    }

}
