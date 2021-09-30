package playing.hall;

import playing.rooms.util.RoomBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDrawer;
import datamodel.RoomsName;
import homepage.HomePageController;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import settings.devices.DevicesSettingsController;
import util.Logger;
import util.gui.load.LoadHelper;
import util.gui.load.Location;

public class PlayingRoomsHallController implements Initializable {

    // === flags ===
    public static int flagForPlayingTimers = 0;
    // === Other Objects ===
    public static ArrayList<Label> roomState_arrayList = new ArrayList<Label>();   //labels
    public static ArrayList<RoomBox> roomBoxes = new ArrayList<>();
    // === GUI Objects ===
    @FXML
    private FlowPane FP;
    public static FlowPane flowPane_Pointer;
    @FXML
    private HBox dailyDetailsBTN;
    @FXML
    private Label dailyDetailsBTN_label;
    @FXML
    private HBox menuBTN;
    @FXML
    private Label menuBTN_label;
    @FXML
    private JFXDrawer drawer1;
    @FXML
    private HBox devicesDiscountBTN;
    @FXML
    private Label devicesDiscountBTN_label;
    @FXML
    private ScrollPane scrollPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println(" >>> >>> >>> PlayingRoomsHallController initialize");
            initMenuButton();
            initDailyDetailsButton();
            initRoomBoxes();

            if (controlpanel.ControlPanel.getInstance().DEVICE_PROMOTIONS) {
                initDevicePromoButton();
            } else {
                ((AnchorPane) devicesDiscountBTN.getParent()).getChildren().remove(devicesDiscountBTN);
                devicesDiscountBTN_label = null;
                devicesDiscountBTN = null;
            }
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + ".initialize() : " + e);
        }
    }

    private void initRoomBoxes() {
        flowPane_Pointer = FP;
        RoomsName.fillData();
        FP.getChildren().clear();
        roomBoxes.clear();
        RoomsName.roomNames.forEach((roomNameObj) -> {
            try {
                RoomBox roomBox = new RoomBox(roomNameObj.getName());
                roomBoxes.add(roomBox);
                roomState_arrayList.add(roomBox.RoomStateText);
                FP.getChildren().add(roomBox);
            } catch (IOException ex) {
                Logger.writeLog("Exception In " + getClass().getName() + ".initRoomBoxes() => roomNames.forEach() : " + ex);
            }
        });
    }

    private void initMenuButton() {
        try {
            menuBTN.getChildren().remove(menuBTN_label);
            menuBTN.setOnMouseEntered((event) -> {
                menuBTN.getChildren().add(menuBTN_label);
            });
            menuBTN.setOnMouseExited((event) -> {
                menuBTN.getChildren().remove(menuBTN_label);
            });

            drawer1.setDefaultDrawerSize(Screen.getPrimary().getVisualBounds().getWidth() * 0.4);
            drawer1.setSidePane(util.gui.load.LoadHelper.getInstance().screenMap.get("root_PlayingPrice"));
            menuBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                if (DevicesSettingsController.actNot == 1) {
                    try {
                        LoadHelper.getInstance().screenMap.put("root_PlayingPrice", FXMLLoader.load(getClass().getResource(Location.getInstance().get("PLAYINGPRICEVIEW"))));
                        drawer1.setSidePane(util.gui.load.LoadHelper.getInstance().screenMap.get("root_PlayingPrice"));
                    } catch (IOException ex) {
                        Logger.writeLog("Exception in " + getClass().getName() + ".initDrawer()->menuBTN.addEventHandler()  :-" + ex);
                    }
                    DevicesSettingsController.actNot = 0;
                }
                drawer1.toggle();
            });
            drawer1.setOnDrawerOpening(e -> drawer1.toFront());
            drawer1.setOnDrawerClosed(e -> drawer1.toBack());
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + ".initDrawer() :-" + e);
        }
    }

    private void initDevicePromoButton() {
        devicesDiscountBTN.visibleProperty().bind(playing.price.PlayingPriceController.IS_DISCOUNT_ACTIVE);
        devicesDiscountBTN.getChildren().remove(devicesDiscountBTN_label);
        devicesDiscountBTN.setOnMouseEntered((event) -> {
            devicesDiscountBTN.getChildren().add(devicesDiscountBTN_label);
        });
        devicesDiscountBTN.setOnMouseExited((event) -> {
            devicesDiscountBTN.getChildren().remove(devicesDiscountBTN_label);
        });
        devicesDiscountBTN.setOnMouseClicked((event) -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(Location.getInstance().get("SHOW_PROMOTED_DEVICES")));
                JFXDialog dialog = new JFXDialog(homepage.HomePageController.homepageStackPane, fxmlLoader.load(), JFXDialog.DialogTransition.TOP);
                ((settings.promotion.device.show.ShowDevicesController) fxmlLoader.getController()).dialog = dialog;
                HomePageController.currentDialog = dialog;
                dialog.show();
            } catch (IOException ex) {
                Logger.writeLog("Exception in " + getClass().getName() + ".initDevicePromoButton - > OnMouseClicked :-" + ex);
            }
        });
    }

    private void initDailyDetailsButton() {
        dailyDetailsBTN.getChildren().remove(dailyDetailsBTN_label);
        dailyDetailsBTN.setOnMouseEntered((event) -> {
            dailyDetailsBTN.getChildren().add(dailyDetailsBTN_label);
        });
        dailyDetailsBTN.setOnMouseExited((event) -> {
            dailyDetailsBTN.getChildren().remove(dailyDetailsBTN_label);
        });
        dailyDetailsBTN.setOnMouseClicked((event) -> {
            toolbar.ToolbarController.showDailyDetails("ps");
        });
    }

    @FXML
    private void scrollHandler(ScrollEvent event) {
        util.gui.HelperMethods.incrementScrollSpeed(event, scrollPane);
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

}
