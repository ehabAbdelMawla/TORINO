package dialog.SpacificTime;

import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.time.LocalDate;
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

public class SpacificTimeController implements Initializable {
    
    private double xOffset = 0;
    private double yOffset = 0;
    public static String f;
    public static String t;
    @FXML
    private JFXDatePicker from;
    @FXML
    private JFXDatePicker TOD;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        f = t = "";
        TOD.setValue(LocalDate.now());
    }
    
    @FXML
    private void closeWindow(Event event) {
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).close();
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
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }
    
    @FXML
    private void SearchEvent(Event event) {
        try {
            if (from.getValue() == null || TOD.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("- يجب تحديد بداية الفترة .");
                return;
            } else if (from.getValue().isAfter(TOD.getValue())) {
                DialogHelper.getInstance().showOKAlert("-الفترة التي ادخلتها غير صحيحة ");
                return;
            }
            f = from.getValue().toString();
            t = TOD.getValue().toString();
            ((Stage) from.getScene().getWindow()).close();
        } catch (Exception e) {
            Logger.writeLog("Exception in SpacificTimeController -> SearchEvent:-  " + e);
        }
    }
    
    @FXML
    private void keyPressedAction(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow(event);
        } else if (event.getCode() == KeyCode.ENTER) {
            SearchEvent(event);
        }
    }
}
