package dialog.edit.playing.prices;

import datamodel.PlayingPriceTableRecord;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Logger;
import util.gui.load.DialogHelper;

public class EditPlayingPricesController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXTextField singlePrice;
    @FXML
    private JFXTextField DeviceNameT;
    @FXML
    private JFXTextField MultoPrice;
    public static PlayingPriceTableRecord Temp;
    public static int flag = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flag = 0;
        DeviceNameT.setText(Temp.deviceName);
        singlePrice.setText(Temp.singlePrice + "");
        MultoPrice.setText(Temp.MultiPrice + "");
    }

    @FXML
    private void closeWindow(Event event) {
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).close();
    }

    @FXML
    private void editAction(Event event) {
        try {
            if (MultoPrice.getText().trim().equals("") || DeviceNameT.getText().trim().equals("") || singlePrice.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                return;
            }
            float s = 0, m = 0;
            try {
                s = Float.parseFloat(singlePrice.getText().trim());
                m = Float.parseFloat(MultoPrice.getText().trim());
                if (s < 0 || m < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                DialogHelper.getInstance().showOKAlert("- البيانات التي ادخلتها غير صحيحة");
                return;
            }
            Temp = new PlayingPriceTableRecord(DeviceNameT.getText(), s, m, Temp.id);
            flag = 1;
            closeWindow(event);
        } catch (Exception e) {
            Logger.writeLog("Exception in EditPlayingPriceTable -> editAction :-" + e);
        }
    }

    @FXML
    private void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void RootMouseDragged(Event event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void qqqq(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editAction((Event) event);
        }
    }
}
