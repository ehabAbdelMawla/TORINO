/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package settings.promotion.device;

import com.jfoenix.controls.JFXToggleButton;
import datamodel.DevicePromotion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DeviceBoxController extends DevicePromotion {

    @FXML
    private JFXToggleButton toggleButton;
    @FXML
    private Label deviceName;

    public DeviceBoxController() {
        super("", false);
    }

    public void setData(DevicePromotion devicePromotion) {
        setDeviceName(devicePromotion.getDeviceName());
        setIsSelected(devicePromotion.isIsSelected());
        this.deviceName.setText(getDeviceName());
        this.toggleButton.setSelected(isIsSelected());
    }

    @FXML
    private void toggleAction() {
        setIsSelected(toggleButton.isSelected());
    }
}
