package playing.price;

import com.jfoenix.controls.JFXToggleButton;
import controlpanel.ControlPanel;
import datamodel.PlayingPriceTableRecord;
import datamodel.TempMethods;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.db.DatabaseHandler;

public class PlayingPriceController implements Initializable {

    public static SimpleBooleanProperty IS_DISCOUNT_ACTIVE = new SimpleBooleanProperty(false);
    public static SimpleStringProperty devicePercentage = new SimpleStringProperty("0");
    @FXML
    private FlowPane flowPane;
    @FXML
    private ScrollPane SP;
    @FXML
    private JFXToggleButton toggleButton;
    @FXML
    private Label deviceDiscountLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DatabaseHandler.updateDevicePromotionsNotations();   // init device promotion flag 
        if (!ControlPanel.getInstance().DEVICE_PROMOTIONS) {
            ((VBox) toggleButton.getParent().getParent()).getChildren().remove(toggleButton.getParent());
        } else {
            deviceDiscountLabel.textProperty().bind(devicePercentage);
            toggleButton.setSelected(IS_DISCOUNT_ACTIVE.get());

            toggleButton.selectedProperty().addListener(e -> {
                if (IS_DISCOUNT_ACTIVE.get()) {
                    DatabaseHandler.updateDevicePromotionState(0);
                    IS_DISCOUNT_ACTIVE.set(Boolean.FALSE);
                } else {
                    DatabaseHandler.updateDevicePromotionState(1);
                    IS_DISCOUNT_ACTIVE.set(Boolean.TRUE);
                }
            });
        }
        initFlowPane();
    }

    private void initFlowPane() {
        flowPane.getChildren().clear();
        ObservableList<PlayingPriceBox> list = FXCollections.observableArrayList();
        ObservableList<PlayingPriceTableRecord> playingData = TempMethods.selectPlayingData();
        for (PlayingPriceTableRecord record : playingData) {
            list.add(new PlayingPriceBox(record.getDeviceName(), record.getSinglePrice(), record.getMultiPrice()));
        }
        flowPane.getChildren().addAll(list);

    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, SP);
    }

    private static class PlayingPriceBox extends VBox {

        private String deviceName;
        private float singlePrice;
        private float multiPrice;

        public PlayingPriceBox(String deviceName, float singlePrice, float multiPrice) {
            this.deviceName = deviceName;
            this.singlePrice = singlePrice;
            this.multiPrice = multiPrice;
            this.getStyleClass().add("playing-price-box");
            this.setAlignment(Pos.CENTER);

            Label deviceNameLabel = new Label(this.deviceName);
            FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.GAMEPAD);

            HBox titlePane = new HBox(deviceNameLabel);
            titlePane.setAlignment(Pos.CENTER);
            titlePane.getStyleClass().add("playing-price-title-pane");

            Label singleTitle = new Label("Single");
            Label singleValue = new Label(String.valueOf(this.singlePrice));
            singleValue.setStyle("-fx-font-size: 30; -fx-font-weight:bold;");
            VBox singleBox = new VBox(singleTitle, singleValue);
            singleBox.setSpacing(10);
            singleBox.setPadding(new Insets(10));
            singleBox.setAlignment(Pos.CENTER);

            Label multiTitle = new Label("Multi");
            Label multiValue = new Label(String.valueOf(this.multiPrice));
            multiValue.setStyle("-fx-font-size: 30; -fx-font-weight:bold;");
            VBox multiBox = new VBox(multiTitle, multiValue);
            multiBox.setSpacing(10);
            multiBox.setPadding(new Insets(10));
            multiValue.setAlignment(Pos.CENTER);

            Separator separator = new Separator(Orientation.VERTICAL);

            HBox pricesBox = new HBox(singleBox, separator, multiBox);
            pricesBox.setSpacing(10);
            pricesBox.setAlignment(Pos.CENTER);

            this.getChildren().addAll(titlePane, pricesBox);
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public float getSinglePrice() {
            return singlePrice;
        }

        public void setSinglePrice(float singlePrice) {
            this.singlePrice = singlePrice;
        }

        public float getMultiPrice() {
            return multiPrice;
        }

        public void setMultiPrice(float multiPrice) {
            this.multiPrice = multiPrice;
        }

        @Override
        public String toString() {
            return "PlayingPriceBox{" + "deviceName=" + deviceName + ", singlePrice=" + singlePrice + ", multiPrice=" + multiPrice + '}';
        }

    }
}
