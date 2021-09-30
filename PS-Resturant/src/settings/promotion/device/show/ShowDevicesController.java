package settings.promotion.device.show;

import com.jfoenix.controls.JFXDialog;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import playing.price.PlayingPriceController;
import util.db.DatabaseHandler;

public class ShowDevicesController implements Initializable {

    public JFXDialog dialog;
    @FXML
    private Label discountPercentage;
    @FXML
    private FlowPane devicesPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        discountPercentage.textProperty().bind(PlayingPriceController.devicePercentage);
        initDevices();
    }

    // fetch devices that has discount from db
    private void initDevices() {
        devicesPane.getChildren().clear();
        DatabaseHandler.selectDeviceNamesWhichApplayPormotion().keySet().forEach((deviceName) -> {
            devicesPane.getChildren().add(new WhiteBox(deviceName));
        });
    }

    private class WhiteBox extends VBox {

        private final Label value;

        public WhiteBox(String value) {
            this.value = new Label(value);
            this.getChildren().add(this.value);
            this.getStyleClass().add("white-pane");
        }

        public String getValue() {
            return value.getText();
        }

        public void setValue(String value) {
            this.value.setText(value);
        }

    }
}
