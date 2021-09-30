package playing.rooms.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import playing.rooms.RoomController;
import util.gui.load.Location;

public class RoomView {

    public final RoomController roomController;
    private final Scene scene;
    private static final Stage STAGE = util.gui.BuilderUI.initStageDecorated(null, "", "");

    public RoomView(RoomBox roomBox) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Location.getInstance().get("ROOM")));
        scene = new Scene(loader.load());
        scene.getStylesheets().add("/resources/style/base.css");
        roomController = loader.getController();
        roomController.roomName.setText(roomBox.getRoomNumber());
        roomController.SetRoomBox(roomBox);
    }
    
    public RoomController getRoomController() {
        return roomController;
    }

    public Stage getStage() {
        return RoomView.STAGE;
    }

    public Scene getScene() {
        return this.scene;
    }
}
