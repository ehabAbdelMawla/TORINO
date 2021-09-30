package dialog.alert.ok;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.validation.SingleInstance;

public class OkAlertController implements Initializable {

    @FXML
    private Label header;
    private double xOffset = 0;
    private double yOffset = 0;
    public static Label text;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        text = header;
    }

    @FXML
    private void closeWindow(Event event) {
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).close();
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
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            closeWindow(event);
        }
    }

}
