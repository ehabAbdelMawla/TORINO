package dialog.alert.confirm;

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

public class ConfirmAlertController implements Initializable {

    @FXML
    private Label header;
    private double xOffset = 0;
    private double yOffset = 0;
    public static Label text;
    public static int flag;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(" >>> >>> >>> ConfirmAlertController initialize");
        text = header;
        flag = 0;
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
    private void okAction(Event event) {
        flag = 1;
        closeWindow(event);
    }

    @FXML
    private void keyAct(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            okAction(event);
        } else if (event.getCode().equals(KeyCode.ESCAPE)) {
            closeWindow(event);
        }
    }

}
