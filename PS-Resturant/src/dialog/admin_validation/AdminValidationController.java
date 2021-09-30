package dialog.admin_validation;

import datamodel.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
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
import util.Logger;
import util.db.DatabaseHandler;

public class AdminValidationController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    public static boolean flag;
    @FXML
    private JFXPasswordField PasswordField;
    @FXML
    private JFXTextField PasswordTextField;
    @FXML
    private Label erorrText;
    public static String tempAdminpass = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tempAdminpass = "";
        flag = false;
        erorrText.setVisible(false);
        PasswordTextField.setVisible(false);
        PasswordTextField.textProperty().bindBidirectional(PasswordField.textProperty());
    }

    @FXML
    private void validationAction(Event event) {
        if (isAdmin(PasswordTextField.getText())) {
            flag = true;
            erorrText.setVisible(false);
            tempAdminpass = PasswordTextField.getText();
            closeAction(event);
        } else {
            erorrText.setVisible(true);
        }
    }

    @FXML
    private void closeAction(Event event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
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

    public boolean isAdmin(String pass) {
        try {
            String sql = "select * from users where password=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, pass);
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            String a, b, c;
            int d;
            User rec;
            while (DatabaseHandler.result.next()) {
                a = DatabaseHandler.result.getString(1);
                b = DatabaseHandler.result.getString(2);
                c = DatabaseHandler.result.getString(3);
                d = DatabaseHandler.result.getInt(4);
                rec = new User(a, b, c, d);
                if ((c.equalsIgnoreCase("أدمن") || c.equalsIgnoreCase("مالك")) && b.equals(pass)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
           Logger.writeLog("Exception in adminValidaTionController->is Admin :- " + e);
        }
        return false;
    }

    @FXML
    private void eyeBTN(MouseEvent event) {
        PasswordField.setVisible(false);
        PasswordTextField.setVisible(true);
    }

    @FXML
    private void eyeBTN2(MouseEvent event) {
        PasswordField.setVisible(true);
        PasswordTextField.setVisible(false);
    }

    @FXML
    private void kEve(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validationAction(event);
        } else if (event.getCode() == KeyCode.ESCAPE) {
            closeAction((Event) event);
        }
    }

}
