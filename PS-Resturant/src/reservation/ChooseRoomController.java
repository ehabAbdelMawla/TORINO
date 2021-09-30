package reservation;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.gui.load.DialogHelper;

public class ChooseRoomController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXComboBox<String> roomsComboBox;
    public String roomChoosen = "";
    private final ObservableList<String> roomsObservableList = FXCollections.observableArrayList();

    public void initData(ObservableList<String> availableRooms) {
        roomsObservableList.setAll(availableRooms);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roomsComboBox.setItems(roomsObservableList);
    }

    @FXML
    private void closeAction(ActionEvent event) {
        roomChoosen = "";
        ((Stage) roomsComboBox.getScene().getWindow()).close();
    }

    @FXML
    private void confirmAction(ActionEvent event) {
        if (roomsComboBox.getValue() != null) {
            roomChoosen = roomsComboBox.getValue();
            ((Stage) roomsComboBox.getScene().getWindow()).close();
        } else {
            DialogHelper.getInstance().showOKAlert("يجب اختيار حجرةً");
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
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) event.getSource()).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

}
