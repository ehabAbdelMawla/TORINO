package playing.rooms.util;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;

public class RoomBox extends VBox {

    private final Label RoomNumber;
    private String RoomNum;
    private final JFXButton RoomButton;
    public ImageView RoomStateIcon;
    private static final Image AVAILABLE_IMG = new Image("/resources/image/monitor_Av.png");
    private static final Image BUSY_IMG = new Image("/resources/image/monitor_BS.png");
    public Label RoomStateText;
    private boolean AvailableState;
    public RoomView roomView;

    public RoomBox(String roomName) throws IOException {
        // RoomNumber
        this.RoomNum = roomName;
        this.RoomNumber = new Label(roomName);
        this.RoomNumber.setFont(Font.font(41));
        // RoomButton
        this.RoomButton = new JFXButton();
        this.RoomButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.RoomButton.setOnAction(goToRoom());
        this.RoomButton.setFocusTraversable(false);
        // RoomStateIcon
        AvailableState = true;
        this.RoomStateIcon = new ImageView(AVAILABLE_IMG);
        this.RoomStateIcon.setFitWidth(160);
        this.RoomStateIcon.setFitHeight(160);
        this.RoomButton.setGraphic(RoomStateIcon);
        // RoomStateText
        this.RoomStateText = new Label("متاح");
        this.RoomStateText.setFont(Font.font(41));
        // MainBox
        this.getChildren().addAll(RoomNumber, RoomButton, RoomStateText);
        this.setStyle("-fx-background-color: -fx-secondary-text;"
                + "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 20, 0, 0, 0);"
                + "-fx-min-width:187;"
                + "-fx-min-height:320;"
                + "-fx-background-radius:15;");
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setFillWidth(true);
        this.setAlignment(Pos.CENTER);

        // RoomView
        this.roomView = new RoomView(this);

        //        Set Room State
        roomView.getRoomController().captionOfRoom();
    }

    public void setBusy() {
        AvailableState = false;
        this.RoomStateIcon.setImage(BUSY_IMG);
        this.RoomStateText.setText("مشغول");
    }

    public void setAvailable() {
        AvailableState = true;
        this.RoomStateIcon.setImage(AVAILABLE_IMG);
        this.RoomStateText.setText("متاح");
    }

    public String getRoomNumber() {
        return this.RoomNum;
    }

    public void setRoomNumber(String roomNum) {
        this.RoomNum = roomNum;
        this.RoomNumber.setText(roomNum);
    }

    public Label getRoomNumberLabel() {
        return this.RoomNumber;
    }

    public boolean isAvailable() {
        return this.AvailableState;
    }

    public Stage getStage() {
        return roomView.getStage();
    }

    private EventHandler<ActionEvent> goToRoom() {
        return (ActionEvent e) -> {
            try {
                roomView.getRoomController().updateTotalPRice();
                roomView.getRoomController().UpdateFinalPriceLabel();
                roomView.getRoomController().setCustomerName();
                roomView.getStage().setScene(roomView.getScene());
                roomView.getStage().setTitle(RoomNum);
                roomView.getStage().showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.writeLog("Exception in " + getClass().getName() + ".goToRoom() : " + ex);
            }
        };
    }

    @Override
    public String toString() {
        return "RoomBox{" + "RoomNum=" + RoomNum + '}';
    }

    public static boolean isResumeOptionNeeded() {
        try {
            System.out.println("isResumeOptionNeeded");
            ResultSet res = DatabaseHandler.con.prepareStatement("SELECT count(*) FROM room1playing WHERE status=1").executeQuery();
            while (res.next()) {
                return res.getInt(1) > 0;
            }
        } catch (Exception ex) {
            Logger.writeLog("Exception in RoomBox.isResumeOptionNeeded() : " + ex);
        }
        return false;
    }
}
