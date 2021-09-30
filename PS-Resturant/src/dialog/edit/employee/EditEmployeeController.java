package dialog.edit.employee;

import datamodel.User;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Logger;
import util.gui.load.DialogHelper;

public class EditEmployeeController implements Initializable {

    public static User us;
    public static int flag = 0;
    public static int accessFalg = 0;

    // === GUI Objects ===
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXPasswordField pass;
    @FXML
    private JFXRadioButton adRadio;
    @FXML
    private ToggleGroup sla7ia;
    @FXML
    private JFXRadioButton emRadio;
    @FXML
    private JFXTextField passText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flag = 0;
        passText.setVisible(false);
        passText.textProperty().bindBidirectional(pass.textProperty());
        if (accessFalg != 1) {
            adRadio.setDisable(true);
            emRadio.setDisable(true);
        }
        if (us.aceessConstrains.equalsIgnoreCase("مالك")) {
            adRadio.setVisible(false);
            emRadio.setVisible(false);
        } else if (us.aceessConstrains.equalsIgnoreCase("أدمن")) {
            adRadio.setSelected(true);
        }
        name.setText(us.userName);
        pass.setText(us.password);
        passText.setText(us.password);
    }

    @FXML
    private void closeWindow(Event event) {
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).close();
    }

    @FXML
    private void editAction(Event event) {
        try {
            if (name.getText().trim().equals("") || pass.getText().trim().equals("")) {
                DialogHelper.getInstance().showOKAlert("- يجب ادخال جميع البيانات");
                return;
            }
            String access = "موظف";
            if (adRadio.isSelected()) {
                access = "أدمن";
            }
            if (us.aceessConstrains.equalsIgnoreCase("مالك")) {
                access = "مالك";
            }
            us = new User(name.getText(), pass.getText(), access, us.active);
            flag = 1;
            closeWindow(event);
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> editAction in :-" + e);
        }
    }

    @FXML
    private void RootMouseDragged(MouseEvent event) {
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    private void RootMousePressed(MouseEvent event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    private void KeyEv(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editAction(event);
        } else if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow((Event) event);
        }
    }

    @FXML
    private void eyeBTN2(MouseEvent event) {
        passText.setVisible(false);
        pass.setVisible(true);
    }

    @FXML
    private void eyeBTN(MouseEvent event) {
        passText.setVisible(true);
        pass.setVisible(false);
    }
}
