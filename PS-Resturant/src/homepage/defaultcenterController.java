package homepage;

import com.jfoenix.controls.JFXDrawer;
import datamodel.RoomsName;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import playing.rooms.util.RoomBox;
import settings.devices.DevicesSettingsController;
import util.Logger;
import util.gui.load.LoadHelper;
import util.gui.load.Location;

public class defaultcenterController implements Initializable {

    public static int flagForPlayingTimers = 0;

    public static ArrayList<Label> roomState_arrayList = new ArrayList<Label>();   //labels
    @FXML
    private FlowPane FP;
    public static FlowPane FpPointer;
    public static int ROOM_COUNT;
    public static ArrayList<RoomBox> roomBoxes = new ArrayList<>();
    @FXML
    private ScrollPane SP;
    @FXML
    private HBox dailyDetailsBTN;
    @FXML
    private Label dailyDetailsBTN_label;
    @FXML
    private HBox menuBTN;
    @FXML
    private Label menuBTN_label;
    @FXML
    private StackPane stackPane;
    @FXML
    private JFXDrawer drawer1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            menuBTN.getChildren().remove(menuBTN_label);
            menuBTN.setOnMouseEntered((event) -> {
                menuBTN.getChildren().add(menuBTN_label);
            });
            menuBTN.setOnMouseExited((event) -> {
                menuBTN.getChildren().remove(menuBTN_label);
            });

            dailyDetailsBTN.getChildren().remove(dailyDetailsBTN_label);
            dailyDetailsBTN.setOnMouseEntered((event) -> {
                dailyDetailsBTN.getChildren().add(dailyDetailsBTN_label);
            });
            dailyDetailsBTN.setOnMouseExited((event) -> {
                dailyDetailsBTN.getChildren().remove(dailyDetailsBTN_label);
            });

            initDrawer();

            FpPointer = FP;
            FP.getChildren().clear();
            RoomsName.fillData();
            ROOM_COUNT = RoomsName.roomNames.size();
            RoomBox box;
            roomBoxes.clear();
            for (int roomIndex = 0; roomIndex < ROOM_COUNT; roomIndex++) {
                box = new RoomBox(RoomsName.roomNames.get(roomIndex).getName());
                roomBoxes.add(box);
                roomState_arrayList.add(box.RoomStateText);
                FP.getChildren().add(box);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception In " + getClass().getName() + ".initialize() : " + e);
        }
    }

    private void initDrawer() {
        try {
            drawer1.setDefaultDrawerSize(Screen.getPrimary().getVisualBounds().getWidth() * 0.4);
            drawer1.setSidePane(util.gui.load.LoadHelper.getInstance().screenMap.get("root_PlayingPrice"));
            menuBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (DevicesSettingsController.actNot == 1) {
                    try {
                        LoadHelper.getInstance().screenMap.put("root_PlayingPrice", FXMLLoader.load(getClass().getResource(Location.getInstance().get("PLAYINGPRICEVIEW"))));
                        drawer1.setSidePane(util.gui.load.LoadHelper.getInstance().screenMap.get("root_PlayingPrice"));
                    } catch (Exception ex) {
                        Logger.writeLog("Exception in " + getClass().getName() + ".initDrawer()->menuBTN.addEventHandler()  :-" + ex);
                    }
                    DevicesSettingsController.actNot = 0;
                }
                drawer1.toggle();
            });
            drawer1.setOnDrawerOpening(e -> drawer1.toFront());
            drawer1.setOnDrawerClosed(e -> drawer1.toBack());
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".initDrawer() :-" + e);
        }
    }

    public static void getPlayingTimer() {
        flagForPlayingTimers = 0;
        LocalTime t;
        for (int i = 0; i < roomState_arrayList.size(); i++) {
            try {
                t = LocalTime.parse(roomState_arrayList.get(i).getText());
                flagForPlayingTimers = 1;
                break;
            } catch (Exception e) {
            }
        }
    }

    public static void getPlayingStatus() {
        flagForPlayingTimers = 0;
        for (int i = 0; i < roomState_arrayList.size(); i++) {
            if (!(roomState_arrayList.get(i).getText().equals("متاح"))) {
                flagForPlayingTimers = 1;
                break;
            }
        }
    }

    @FXML
    private void goToDailyDetails(Event event) {
        toolbar.ToolbarController.showDailyDetails("ps");
    }

}
