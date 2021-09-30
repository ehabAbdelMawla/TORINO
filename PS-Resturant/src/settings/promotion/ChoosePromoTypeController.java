/*
 * Code Clinic Solutions
 * PS-Restaurant System  * 
 */
package settings.promotion;

import com.jfoenix.controls.JFXDialog;
import homepage.HomePageController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import util.Logger;
import util.gui.load.Location;

public class ChoosePromoTypeController {

    public JFXDialog dialog;

    @FXML
    private void goToDevicePromo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("DEVICE_PROMOTIONS")));
            JFXDialog jFXDialog = new JFXDialog(homepage.HomePageController.homepageStackPane, fxmlLoader.load(), JFXDialog.DialogTransition.TOP);
            ((settings.promotion.device.DevicePromoController) fxmlLoader.getController()).dialog = jFXDialog;
            HomePageController.currentDialog = jFXDialog;
            jFXDialog.show();
            close();
        } catch (IOException ex) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> goToDevicePromo() -> " + ex);
        }
        close();
    }

    @FXML
    private void goToHourPromo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("HOUR_PROMOTIONS")));
            JFXDialog jFXDialog = new JFXDialog(homepage.HomePageController.homepageStackPane, fxmlLoader.load(), JFXDialog.DialogTransition.TOP);
            settings.promotion.HourPromotionController controller = fxmlLoader.getController();

            controller.dialog = jFXDialog;
            HomePageController.currentDialog = jFXDialog;
            jFXDialog.show();
            close();
        } catch (IOException ex) {
            Logger.writeLog("Exception in " + getClass().getName() + ".goToHourPromo() :-" + ex);
        }
    }

    private void close() {
        dialog.close();
    }
}
