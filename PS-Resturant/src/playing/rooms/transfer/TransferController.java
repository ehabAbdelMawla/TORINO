package playing.rooms.transfer;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import playing.rooms.util.RoomBox;
import playing.hall.PlayingRoomsHallController;
import playing.rooms.RoomController;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

public class TransferController implements Initializable {

    @FXML
    private JFXComboBox<String> AVRoomsComboBox;
    public static JFXComboBox<String> AVRoomsComboBoxPointer;

    public static String sourceRoom;
    public static int transferClick;
    @FXML
    private JFXTextField fromTextField;
    public static JFXTextField fromTextField_Static;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AVRoomsComboBoxPointer = AVRoomsComboBox;
        fromTextField_Static = fromTextField;
    }

    @FXML
    private void fillAvilableRooms() {
        AVRoomsComboBox.setItems(getAvRoom());
    }

    @FXML
    private void confirm() {
        try {
            if (AVRoomsComboBox.getValue() == null || AVRoomsComboBox.getValue().equals("")) {
                DialogHelper.getInstance().showOKAlert("يجب اختيار غرفة");
                return;
            }
            sourceRoom = AVRoomsComboBox.getValue();
            transferClick = 1;
            discard();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> confirm :- " + e);
        }
    }

    @FXML
    private void discard() {
        ((Stage) AVRoomsComboBox.getScene().getWindow()).close();
    }

    private ObservableList<String> getAvRoom() {
        ObservableList<String> avR = FXCollections.observableArrayList();
        for (int i = 0; i < PlayingRoomsHallController.roomBoxes.size(); i++) {
            avR.add(PlayingRoomsHallController.roomBoxes.get(i).getRoomNumberLabel().getText());
        }
        try {
            String sql = "SELECT roomName FROM room1playing";
            DatabaseHandler.result = DatabaseHandler.con.prepareStatement(sql).executeQuery();
            while (DatabaseHandler.result.next()) {
                avR.remove(DatabaseHandler.result.getString(1));
            }
        } catch (SQLException e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> getAvRoom :- " + e);
        }
        return avR;
    }

    public void updateRoom(String valueOfReloadPage, String valueOfCurrentPage) {
        try {
            RoomBox roomToReload = PlayingRoomsHallController.roomBoxes.stream()
                    .filter(x -> valueOfReloadPage.equals(x.getRoomNumberLabel().getText()))
                    .findAny()
                    .orElse(null);
            if (roomToReload != null) {
                RoomController controller = roomToReload.roomView.getRoomController();
                controller.resetPlayingInputs();
                controller.fillPlayingTable();
                controller.fillDrinkTable();
                controller.updateTotalPRice();
                controller.UpdateFinalPriceLabel();
                controller.roomBox.setBusy();
            }
            roomToReload = PlayingRoomsHallController.roomBoxes.stream()
                    .filter(x -> valueOfCurrentPage.equals(x.getRoomNumberLabel().getText()))
                    .findAny()
                    .orElse(null);
            if (roomToReload != null) {
                RoomController controller = roomToReload.roomView.getRoomController();
                controller.closeWindow();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> updateRoom() :- " + e);
        }
    }

    @FXML
    private void keyHandler(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            discard();
        }
    }
}
