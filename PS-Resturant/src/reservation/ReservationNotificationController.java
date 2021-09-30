/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package reservation;

import datamodel.Reservation;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ReservationNotificationController implements Initializable {

    @FXML
    private Label clientName;
    @FXML
    private Label deviceName;
    @FXML
    private Label time;
    @FXML
    private Label minsText;

    public void setData(String clientName, String deviceName, String time) {
        this.clientName.setText(clientName);
        this.deviceName.setText(deviceName);
        this.time.setText(time);
        minsText.setText(String.format("يوجد حجز بعد %d دقائق", Reservation.mins));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}
