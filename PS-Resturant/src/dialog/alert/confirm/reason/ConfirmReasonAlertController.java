package dialog.alert.confirm.reason;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ConfirmReasonAlertController implements Initializable {

    @FXML
    private Label header;
    private double xOffset = 0;
    private double yOffset = 0;
    public static Label text;
    public static int flag;
    public static String delReson = "";

    @FXML
    private TextField reason;
    @FXML
    private Label error;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        text = header;
        flag = 0;
        error.setVisible(false);
    }

    @FXML
    private void closeWindow(Event event) {
        error.setVisible(false);
        reason.setText("");
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
        if ("".equals(reason.getText().trim())) {
            error.setVisible(true);
            return;
        }
        delReson = reason.getText().trim();
        flag = 1;
        closeWindow(event);
    }

    @FXML
    private void KeyPrs(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow((Event) event);
        } else if (event.getCode() == KeyCode.ENTER) {
            okAction((Event) event);
        }
    }
}
