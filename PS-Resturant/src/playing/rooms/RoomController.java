package playing.rooms;

import controlpanel.ControlPanel;
import util.gui.ComboBoxAutoComplete;
import dialog.alert.confirm.reason.ConfirmReasonAlertController;
import dialog.edit.playing.room.cafe.EditRoomsDrinksController;
import dialog.edit.playing.room.playing.EditPlayingRecordController;
import datamodel.ArgumentsClass;
import datamodel.DailySheetRecord;
import datamodel.DrinkTableRecord;
import datamodel.Promotion;
import datamodel.ReceetClass;
import datamodel.TempMethods;
import datamodel.PlayingRecord;
import datamodel.playingRecordForPrint;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import datamodel.Customer;
import datamodel.Order;
import datamodel.Product;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import settings.products.NewProductController;
import static settings.promotion.HourPromotionController.getTheCurrentPromotion;
import playing.rooms.transfer.TransferController;
import static playing.rooms.transfer.TransferController.sourceRoom;
import util.Logger;
import util.db.DBCRUD;
import util.db.DBField;
import util.gui.HelperMethods;
import util.gui.load.LoadHelper;
import datamodel.User;
import datamodel.settings.Preferences;
import datamodel.settings.PreferencesType;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.sql.PreparedStatement;
import java.util.Map;
import playing.price.PlayingPriceController;
import playing.rooms.util.RoomBox;
import storage.StorageController;
import util.db.DatabaseHandler;
import static util.db.DatabaseHandler.con;
import static util.db.DatabaseHandler.getProductByBarCode;
import util.db.Methods;
import static util.db.Methods.get;
import util.gui.BuilderUI;
import util.gui.load.DialogHelper;
import util.gui.load.Location;
import util.printing.CashDrawer;

public class RoomController implements Initializable {

    // === Helper Objects ===
    public RoomBox roomBox;

    private final SpinnerValueFactory<Integer> HOURS_VALUES = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12, 0);
    private final SpinnerValueFactory<Integer> MINUTES_VALUES = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0, 5);
    private final SpinnerValueFactory<Integer> NUMS_VALUES = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
    // ... Timeline ...
    private final Timeline MAIN_TIMELINE = new Timeline(new KeyFrame(Duration.millis(1000), ae -> incrementTime()));
    private final Timeline BACKUP_TIMELINE = new Timeline(new KeyFrame(Duration.millis(1000 * 60 * 5), ae -> upDatePlayingTable()));
    // ... Time Objects ...
    private LocalTime StartTime;
    private LocalTime EndTime;
    private LocalTime time = LocalTime.parse("00:00:00");
    private final static DateTimeFormatter DATE_TIME_FORMAT_1 = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final static DateTimeFormatter DATE_TIME_FORMAT_2 = DateTimeFormatter.ofPattern("hh:mm a");
    // ... Flags ...
    public static int leaveRoomNotation = 0;
    public static int innerEffectedAction = 0;
    // ... DB ...
    public final static String PLAY_TABLENAME = "room1Playing";
    public final static String DRINKS_TABLENAME = "room1Drink";
    private final ObservableList<PlayingRecord> PLAYING_DATA = FXCollections.observableArrayList();
    private final ObservableList<DrinkTableRecord> DRINK_DATA = FXCollections.observableArrayList();
    // ... Search Combo  ....
    private ComboBoxAutoComplete drinksSearchCombo;
    private ComboBoxAutoComplete devicesSearchCombo;
    private ComboBoxAutoComplete productCategSearchCombo;

    //    .... Resume Notation ....
    private boolean isResume = false;
    // ====== GUI Objects ======
    @FXML
    private AnchorPane AP;
    @FXML
    public Label roomName;
    @FXML
    private Label timerLabel;
    @FXML
    private JFXButton startTimerButton;
    @FXML
    private JFXButton pauseTimerButton;
    @FXML
    private JFXRadioButton singleRadioButton;
    @FXML
    private ToggleGroup playingToggle;
    @FXML
    private JFXRadioButton MultiRadioButton;
    @FXML
    private JFXToggleButton kindOfTime;
    @FXML
    private Spinner<Integer> mins;
    @FXML
    private Spinner<Integer> hours;
    @FXML
    public JFXComboBox<String> DeviceKind;
    @FXML
    private JFXTreeTableView<PlayingRecord> playingTable;
    @FXML
    private TreeTableColumn<PlayingRecord, String> ps;
    @FXML
    private TreeTableColumn<PlayingRecord, String> from;
    @FXML
    private TreeTableColumn<PlayingRecord, String> To;
    @FXML
    private TreeTableColumn<PlayingRecord, String> S_M;
    @FXML
    private TreeTableColumn<PlayingRecord, String> playingPrice;
    @FXML
    private TreeTableColumn<PlayingRecord, Button> edit;
    @FXML
    private Label totalPRiceLabel;
    @FXML
    private JFXTreeTableView<DrinkTableRecord> DrinkTable;
    @FXML
    private TreeTableColumn<DrinkTableRecord, String> DrinkNameCol;
    @FXML
    private TreeTableColumn<DrinkTableRecord, Integer> NumCol;
    @FXML
    private TreeTableColumn<DrinkTableRecord, Float> DrinkPriceCol;
    @FXML
    private TreeTableColumn<DrinkTableRecord, Button> DrinkEditPrice;
    @FXML
    private TreeTableColumn<DrinkTableRecord, Button> DrinkDelCol;
    @FXML
    private JFXComboBox<String> Drinks;
    @FXML
    private Spinner<Integer> num;
    @FXML
    private TextField DisCountTextF;
    @FXML
    private TextField discountPercentage;
    @FXML
    private Label finalPrice;
    @FXML
    private JFXComboBox<String> proCategCombo;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private AnchorPane detailsBox;
    @FXML
    private VBox customerVbox;
    @FXML
    private HBox customerVboxContainer;
    @FXML
    private HBox footerButtonsContainer;
    @FXML
    private JFXButton transferBtn;
    @FXML
    private JFXButton barCodeButton;
    @FXML
    private MaterialDesignIconView barcodeIcon;
    @FXML
    private VBox chooseFromList_Box;
    @FXML
    private VBox chooseFromBarCode_Box;
    @FXML
    private TextField barocdeTextField;
    @FXML
    private JFXButton printButton;
    @FXML
    private JFXButton leaveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            detailsBox.setVisible(false);
            DisCountTextF.setPromptText("0 " + ControlPanel.getInstance().getCurrency());
            discountPercentage.setPromptText("0 %");
            // ==== PlayingTable ====
            ps.setCellValueFactory(new TreeItemPropertyValueFactory<>("deviceName"));
            from.setCellValueFactory(new TreeItemPropertyValueFactory<>("fromTime"));
            To.setCellValueFactory(new TreeItemPropertyValueFactory<>("toTime"));
            S_M.setCellValueFactory(new TreeItemPropertyValueFactory<>("playingStatus"));
            playingPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("playingPrice"));
            edit.setCellValueFactory(new TreeItemPropertyValueFactory<>("EditBtn"));
            HelperMethods.TableColumnAlignment(ps, from, To, S_M, playingPrice, edit);
            TreeItem<PlayingRecord> playingRoot = new RecursiveTreeItem<>(PLAYING_DATA, RecursiveTreeObject::getChildren);
            playingTable.setRoot(playingRoot);
            playingTable.setShowRoot(false);

            // ==== DrinkTable ====
            DrinkNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("DName"));
            NumCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Num"));
            DrinkPriceCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("Tprice"));
            DrinkEditPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("EDitBTN"));
            DrinkDelCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("DELBTN"));
            HelperMethods.TableColumnAlignment(DrinkNameCol, NumCol, DrinkPriceCol, DrinkEditPrice, DrinkDelCol);
            TreeItem<DrinkTableRecord> drinksRoot = new RecursiveTreeItem<>(DRINK_DATA, RecursiveTreeObject::getChildren);
            DrinkTable.setRoot(drinksRoot);
            DrinkTable.setShowRoot(false);

            // ==== Spinners ====
            hours.setValueFactory(HOURS_VALUES);
            mins.setValueFactory(MINUTES_VALUES);
            num.setValueFactory(NUMS_VALUES);

            // ==== ComboBoxs ====
            productCategSearchCombo = new ComboBoxAutoComplete(proCategCombo);

            DeviceKind.setItems(DatabaseHandler.selectPlayingPriceDeviceNames());
            devicesSearchCombo = new ComboBoxAutoComplete(DeviceKind);

            Drinks.setItems(DatabaseHandler.selectDrinksPriceDeviceNames(""));
            drinksSearchCombo = new ComboBoxAutoComplete(Drinks);

            // Open Stage with relative dimensions to the Screen
            util.gui.HelperMethods.setRelativeSize(AP, 0.75, 1000, 658);

            if (!ControlPanel.getInstance().HOUR_PROMOTIONS || !ControlPanel.getInstance().CUSTOMER_DATA) {
                customerVboxContainer.getChildren().remove(customerVbox);
            }

            if (!ControlPanel.getInstance().ROOM_TRANSFER) {
                footerButtonsContainer.getChildren().remove(transferBtn);
            }

            if (!ControlPanel.getInstance().BARCODE_OPTION) {
                ((AnchorPane) barCodeButton.getParent()).getChildren().remove(barCodeButton);
                barCodeButton = null;
            }
            if (!Preferences.getInstance().getBoolean(PreferencesType.Print, "true") || !Preferences.getInstance().getBoolean(PreferencesType.Print_PlayingRooms, "true")) {
                ((HBox) printButton.getParent()).getChildren().remove(printButton);
                printButton = null;
                leaveButton.setStyle("-fx-background-radius:15;");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.writeLog("Exception in " + getClass().getName() + ".initialize() :-" + ex);
        }
    }

    @FXML
    public void closeWindow() {
        if (detailsBox.isVisible()) {
            detailsBox.setVisible(false);
        }
        ((Stage) (AP.getScene().getWindow())).close();
    }

    @FXML
    private void startTimer() {
        try {
            String errorMSG = "";
            if (DeviceKind.getValue() == null) {
                errorMSG += "-يجب تحديد نوع الجهاز";
                errorMSG += "\n";
            }
            if (!(kindOfTime.isSelected())) {
                if (hours.getValue() == 0 && mins.getValue() == 0) {
                    errorMSG += "- يجب تحديد الوقت";
                }
            }
            if (!(errorMSG.equals(""))) {
                DialogHelper.getInstance().showOKAlert(errorMSG);
                return;
            }
            if (!(kindOfTime.isSelected())) {
                EndTime = LocalTime.parse("00:00:00").plusHours(hours.getValue()).plusMinutes(mins.getValue());
            }
            String noteMSG;
            if (!isResume) {
                noteMSG = User.CurrentUser.userName + " " + " : " + " بدء الوقت.";
            } else {
                noteMSG = User.CurrentUser.userName + " " + " : " + " إستأنف الوقت.";
            }
            DatabaseHandler.insertIntoRoomsNote("play", noteMSG, roomName.getText());
            MAIN_TIMELINE.setCycleCount(Animation.INDEFINITE);
            MAIN_TIMELINE.play();
            BACKUP_TIMELINE.setCycleCount(Animation.INDEFINITE);
            BACKUP_TIMELINE.play();

            timerLabel.setText(time.format(DATE_TIME_FORMAT_1));
            EndCurrntTimer();
            playingInputsControll(true);
            StartTime = isResume ? StartTime : LocalTime.now();
            roomBox.setBusy();
            String kind = "Single";
            if (MultiRadioButton.isSelected()) {
                kind = "Multi";
            }
            DatabaseHandler.insertInPlayingTable(new PlayingRecord(0,
                    DeviceKind.getValue(),
                    StartTime.format(DATE_TIME_FORMAT_2),
                    (StartTime.plusHours(time.getHour()).plusMinutes(time.getMinute()).format(DATE_TIME_FORMAT_2)),
                    kind,
                    DatabaseHandler.getPrice(time.getHour(),
                            time.getMinute(),
                            kind,
                            DeviceKind.getValue()),
                    1,
                    roomName.getText(),
                    EndTime == null ? "" : EndTime.format(DATE_TIME_FORMAT_1)
            ));
        } catch (Exception e) {
            Logger.writeLog("Exception in RoomController ->  startTimer " + e);
        }
    }

    @FXML
    private void pauseTimer() {
        if (MAIN_TIMELINE.getStatus().equals(Animation.Status.PAUSED)) {
            MAIN_TIMELINE.play();
            BACKUP_TIMELINE.play();
            pauseTimerButton.setText("ايقاف موقت");
        } else if (MAIN_TIMELINE.getStatus().equals(Animation.Status.RUNNING)) {
            MAIN_TIMELINE.pause();
            BACKUP_TIMELINE.pause();
            pauseTimerButton.setText("أستئناف");
        }
    }

    @FXML
    private void endTimer() {
        if (startTimerButton.isDisable()) {
            String nnn = User.CurrentUser.userName + " " + " : " + "انهى الوقت.";
            DatabaseHandler.insertIntoRoomsNote("play", nnn, roomName.getText());
            MAIN_TIMELINE.stop();
            BACKUP_TIMELINE.stop();
            roomBox.setBusy();
            upDatePlayingTable();
            EndCurrntTimer();
            fillPlayingTable();
            updateTotalPRice();
            UpdateFinalPriceLabel();
            resetPlayingInputs();
            playingInputsControll(false);
        }
    }

    @FXML
    private void diabledMin_HourInputs() {
        if (kindOfTime.isSelected()) {
            hours.getValueFactory().setValue(0);
            mins.getValueFactory().setValue(0);
            hours.setDisable(true);
            mins.setDisable(true);
        } else {
            hours.setDisable(false);
            mins.setDisable(false);
        }
    }

    @FXML
    private void fillDeviceNames() {
        DeviceKind.setItems(DatabaseHandler.selectPlayingPriceDeviceNames());
        devicesSearchCombo.setItems(DeviceKind.getItems());
    }

    public void incrementTime() {
        try {
            /* Always Increase Time Object */
            time = time.plusSeconds(1); 
            if (EndTime != null) {
                /* Decrese Label Value  */
                java.time.Duration duration = java.time.Duration.between(time, EndTime);
                long seconds = Math.abs(duration.getSeconds());
                String timeVal = String.format("%d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
                timerLabel.setText(timeVal);
                roomBox.RoomStateText.setText(timeVal);

                /* Check If Time Is End */
                if (EndTime.equals(time)) {
                    MAIN_TIMELINE.stop();
                    BACKUP_TIMELINE.stop();
                    upDatePlayingTable();
                    EndCurrntTimer();
                    updateTotalPRice();
                    UpdateFinalPriceLabel();
                    fillPlayingTable();
                    resetPlayingInputs();
                    playingInputsControll(false);
                    roomBox.setBusy();
                    BuilderUI.showTimerNotification(roomName.getText());
                }

            } else {
                /* Increase  Label Value  */
                timerLabel.setText(time.format(DATE_TIME_FORMAT_1));
                roomBox.RoomStateText.setText(time.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Eception in " + getClass().getName() + " -> IncrementTime:" + e);
        }
    }

    public void resetPlayingInputs() {
        kindOfTime.setSelected(false);
        time = LocalTime.parse("00:00:00");
        singleRadioButton.setSelected(true);
        timerLabel.setText("00:00:00");
        pauseTimerButton.setText("ايقاف موقت");
        MAIN_TIMELINE.stop();
        BACKUP_TIMELINE.stop();
        hours.getValueFactory().setValue(0);
        mins.getValueFactory().setValue(0);
        EndTime = null;
        DeviceKind.setValue(null);
        isResume = false;
    }

    public String captionOfRoom() {
        String MyCaption = "متاح";
        try {
            String sqlState = "SELECT SUM(num) FROM ("
                    + "select count(*) as num FROM room1playing Where roomName='" + roomName.getText() + "'"
                    + "UNION ALL "
                    + " select count(*) as num FROM room1drink Where roomName='" + roomName.getText() + "'"
                    + " )";

            ResultSet result = DatabaseHandler.con.prepareStatement(sqlState).executeQuery();
            int count = 0;
            while (result.next()) {
                count = result.getInt(1);
                break;
            }
            if (count > 0) {
                roomBox.setBusy();
                return MyCaption;
            }
            result.close();
            DatabaseHandler.stat.close();
            roomBox.setAvailable();
        } catch (Exception e) {
            Logger.writeLog("Exception in roomController -> captionOfRoom :-" + e);
        }
        return MyCaption;
    }

    /**
     *
     * @param isDisable : true to disable & false to enable
     */
    public void playingInputsControll(boolean isDisable) {
        singleRadioButton.setDisable(isDisable);
        MultiRadioButton.setDisable(isDisable);
        startTimerButton.setDisable(isDisable);
        kindOfTime.setDisable(isDisable);
        hours.setDisable(isDisable);
        mins.setDisable(isDisable);
        DeviceKind.setDisable(isDisable);
    }

    public void upDatePlayingTable() {
        try {
            String kind = "Single";
            if (MultiRadioButton.isSelected()) {
                kind = "Multi";
            }
            String sqlString = "update " + PLAY_TABLENAME + " set EdTime=? ,price =? where roomName=? And status=1";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, (StartTime.plusHours(time.getHour()).plusMinutes(time.getMinute()).format(DATE_TIME_FORMAT_2)));
            DatabaseHandler.stat.setString(2, DatabaseHandler.getPrice(time.getHour(), time.getMinute(), kind, DeviceKind.getValue()));
            DatabaseHandler.stat.setString(3, roomName.getText());
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception In RoomController -> upDatePlayingTable:-" + e);
        }
    }

    public ObservableList<PlayingRecord> selectPlayingData() {
        ObservableList<PlayingRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM " + PLAY_TABLENAME + " where roomName=? And status!=1";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, roomName.getText());
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();

            PlayingRecord rec;
            while (DatabaseHandler.result.next()) {
                rec = new PlayingRecord(DatabaseHandler.result.getInt(1),
                        DatabaseHandler.result.getString(2),
                        DatabaseHandler.result.getString(3),
                        DatabaseHandler.result.getString(4),
                        DatabaseHandler.result.getString(5),
                        DatabaseHandler.result.getString(6),
                        DatabaseHandler.result.getInt(8),
                        DatabaseHandler.result.getString(7),
                        DatabaseHandler.result.getString(9));
                rec.getEditBtn().setOnAction(EditPlayingRecord(rec));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> selectPlayingData:-" + e);
            return list;
        }
    }

    public PlayingRecord selectActivePlayingRecordAndRemove() {
        PlayingRecord rec = null;
        try {
            String sqlState = "SELECT * FROM " + PLAY_TABLENAME + " where roomName=? and status=1";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, roomName.getText());
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();

            while (DatabaseHandler.result.next()) {
                rec = new PlayingRecord(DatabaseHandler.result.getInt(1),
                        DatabaseHandler.result.getString(2),
                        DatabaseHandler.result.getString(3),
                        DatabaseHandler.result.getString(4),
                        DatabaseHandler.result.getString(5),
                        DatabaseHandler.result.getString(6),
                        DatabaseHandler.result.getInt(8),
                        DatabaseHandler.result.getString(7),
                        DatabaseHandler.result.getString(9));
            }
            DatabaseHandler.result.close();
            DatabaseHandler.con.prepareStatement("Delete FROM " + PLAY_TABLENAME + " Where roomName='" + roomName.getText() + "' and status=1").execute();
        } catch (Exception e) {
            Logger.writeLog("Exception In " + getClass().getName() + " -> selectPlayingData:-" + e);
        }
        return rec;
    }

    public void fillPlayingTable() {
        PLAYING_DATA.setAll(selectPlayingData());
    }

    public EventHandler<ActionEvent> EditPlayingRecord(PlayingRecord playingRecord) {
        return (ActionEvent event) -> {
            try {
                EditPlayingRecordController.previousRecord = playingRecord;
                Stage stage = util.gui.BuilderUI.initStageUnDecorated(new Scene(FXMLLoader.load(getClass().getResource(Location.getInstance().get("EDIT_ROOMS_PLAYING")))), "Edit", "info");
                stage.showAndWait();
                AP.requestFocus();
                if (EditPlayingRecordController.flag == 1 && !EditPlayingRecordController.note.equals("")) {
                    PlayingRecord Newrec = EditPlayingRecordController.previousRecord;
                    String sql = "update " + PLAY_TABLENAME + " set device=? , stTime=? , EdTime=? , kind=? , price=? Where id=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setString(1, Newrec.getDeviceName());
                    DatabaseHandler.stat.setString(2, Newrec.getFromTime());
                    DatabaseHandler.stat.setString(3, Newrec.getToTime());
                    DatabaseHandler.stat.setString(4, Newrec.getPlayingStatus());
                    DatabaseHandler.stat.setString(5, Newrec.getPlayingPrice());
                    DatabaseHandler.stat.setInt(6, playingRecord.getId());
                    DatabaseHandler.stat.execute();
                    DatabaseHandler.stat.close();
                    DatabaseHandler.insertIntoRoomsNote("play", EditPlayingRecordController.note, roomName.getText());
                    EditPlayingRecordController.note = "";
                    fillPlayingTable();
                    updateTotalPRice();
                    UpdateFinalPriceLabel();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception In " + getClass().getName() + ".EditPlayingRecord() :-" + e);
            }
        };
    }

    public void updateTotalPRice() {
        double tP = 0;
        tP += get("SELECT SUM(price) FROM " + PLAY_TABLENAME + " where roomName='" + roomName.getText() + "' And status=0");
        tP += get("SELECT SUM(price) FROM " + DRINKS_TABLENAME + " where roomName='" + roomName.getText() + "'");
        totalPRiceLabel.setText(HelperMethods.formatNum(tP + (Order.getTaxAndServantPercentage("rooms") * tP)) + ControlPanel.getInstance().getCurrency());
    }

    @FXML
    private void addDrink() {
        try {
            if (PLAYING_DATA.isEmpty() && MAIN_TIMELINE.getStatus().equals(Animation.Status.STOPPED) && !ControlPanel.getInstance().DRINKS_WITHOUT_TIMER) {
                DialogHelper.getInstance().showOKAlert("- يجب بدء الوقت");
                return;
            }
            if (proCategCombo.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("-يجب تحديد الفئة ");
                return;
            }
            if (Drinks.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("- يجب تحديد المشروب ");
                return;
            }

            boolean result = buyDrink(proCategCombo.getValue().trim(), Drinks.getValue().trim(), num.getValue());
            if (result) {
                Drinks.setValue(null);
                num.getValueFactory().setValue(0);
            }

        } catch (Exception e) {
            Logger.writeLog("Exception in Room1Controller -> addDrink:- " + e);
        }
    }

    public boolean buyDrink(String categ, String product, int num) {
        String argsString = StorageController.getArgsOfProduct(categ, product);
        ObservableList<ArgumentsClass> argList = Product.convertStringToListOfArgs(argsString);
        int x = StorageController.ableToBuy(product, argList, num);
        if (x == -5) {
            DialogHelper.getInstance().showOKAlert("هذا العدد غير متوافر!");
            return false;
        }
        float price = DatabaseHandler.getDrinkPrice("عميل", categ, product);
        if (price < 0) {
            DialogHelper.getInstance().showOKAlert("- هذا المنتج غير موجود!");
            return false;
        }
        StorageController.pullFromORPushTOStore(product, argList, num, "pull");
        float Tprice = price * num;

        /* Check if Exists Before */
        DrinkTableRecord isExists = DRINK_DATA.stream().filter(pro -> pro.DName.equalsIgnoreCase(product))
                .findAny().orElse(null);

        if (isExists == null) {
            /* Add New Record */
            DatabaseHandler.insertInDrinkTable(new DrinkTableRecord((int) Methods.GetMaximum("id", DRINKS_TABLENAME),
                    product,
                    num,
                    Tprice,
                    roomName.getText(), argsString, categ
            ));
        } else {
            try {
                /* Update Previous Record */
                PreparedStatement stat = con.prepareStatement("Update " + DRINKS_TABLENAME + " SET num=num+? , price=price+?  Where id=?");
                stat.setInt(1, num);
                stat.setFloat(2, Tprice);
                stat.setInt(3, isExists.getId());
                stat.execute();
            } catch (Exception e) {
                Logger.writeLog("Exception In RoomSController -> BuyDrink (Update Exists):-" + e);
            }

        }
        /* Update Page Data */
        roomBox.setBusy();
        innerEffectedAction = 1;
        fillDrinkTable();
        updateTotalPRice();
        UpdateFinalPriceLabel();
        return true;
    }

    public void fillDrinkTable() {
        DRINK_DATA.setAll(selectDrinkData());

        /*
        - No Other orders 
        - No Running Time
        - No Prev Playing records
         */
        if (DRINK_DATA.isEmpty() && PLAYING_DATA.isEmpty() && !startTimerButton.isDisable()) {
            roomBox.setAvailable();
        }
    }

    public ObservableList<DrinkTableRecord> selectDrinkData() {
        ObservableList<DrinkTableRecord> list = FXCollections.observableArrayList();
        try {
            list.clear();
            String sqlState = "SELECT * FROM " + DRINKS_TABLENAME + " where roomName=? ORDER BY id ASC";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlState);
            DatabaseHandler.stat.setString(1, roomName.getText());
            DatabaseHandler.result = DatabaseHandler.stat.executeQuery();
            int idd, b;
            float c;
            String a, d, args, catg;
            DrinkTableRecord rec;
            while (DatabaseHandler.result.next()) {
                idd = DatabaseHandler.result.getInt(1);
                a = DatabaseHandler.result.getString(2);
                d = DatabaseHandler.result.getString(3);
                b = DatabaseHandler.result.getInt(4);
                c = DatabaseHandler.result.getFloat(5);
                args = DatabaseHandler.result.getString(6);
                catg = DatabaseHandler.result.getString(7);
                rec = new DrinkTableRecord(idd, d, b, c, a, args, catg);
                rec.DELBTN.setOnAction(DeleteDrinkRecord(rec));
                rec.EDitBTN.setOnAction(EditDrinkRecord(rec));
                list.add(rec);
            }
            DatabaseHandler.result.close();
            return list;
        } catch (Exception e) {
            Logger.writeLog("Exception In RoomSController -> selectDrinkData :-" + e);
            return list;
        }
    }

    public EventHandler<ActionEvent> EditDrinkRecord(DrinkTableRecord rec) {
        return (ActionEvent event) -> {
            try {
                EditRoomsDrinksController.updated = rec;
                Parent root = FXMLLoader.load(getClass().getResource(Location.getInstance().get("EDIT_ROOMS_DRINK")));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                AP.requestFocus();
                if (EditRoomsDrinksController.flag == 1 && !EditRoomsDrinksController.note.equals("")) {
                    DrinkTableRecord newRec = EditRoomsDrinksController.updated;
                    String sql = "update " + DRINKS_TABLENAME + " SET drinkName=?,num=? ,price=? ,proArgs=? ,categ=? Where id=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setInt(6, rec.id);
                    DatabaseHandler.stat.setString(1, newRec.DName);
                    DatabaseHandler.stat.setInt(2, newRec.Num);
                    DatabaseHandler.stat.setFloat(3, newRec.Tprice);
                    DatabaseHandler.stat.setString(4, NewProductController.convertArgsListToString(newRec.proarguments));
                    DatabaseHandler.stat.setString(5, newRec.categ);
                    DatabaseHandler.stat.execute();
                    DatabaseHandler.stat.close();
                    innerEffectedAction = 1;
                    fillDrinkTable();
                    DatabaseHandler.insertIntoRoomsNote("Cafe", EditRoomsDrinksController.note, roomName.getText());
                    EditRoomsDrinksController.note = "";
                    updateTotalPRice();
                    UpdateFinalPriceLabel();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception In " + getClass().getName() + ".EditDrinkRecord() :- " + e);
            }
        };
    }

    public EventHandler<ActionEvent> DeleteDrinkRecord(DrinkTableRecord rec) {
        return (ActionEvent event) -> {
            try {
                DialogHelper.getInstance().showConfirmAlertWithReason("هل انت متأكد من حذف الطلب؟");
                AP.requestFocus();
                if (ConfirmReasonAlertController.flag == 1) {
                    ConfirmReasonAlertController.flag = 0;
                    StorageController.pullFromORPushTOStore(rec.DName, rec.proarguments, rec.Num, "push");
                    String sql = "DELETE FROM " + DRINKS_TABLENAME + " Where id=?";
                    DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
                    DatabaseHandler.stat.setInt(1, rec.id);
                    DatabaseHandler.stat.execute();
                    innerEffectedAction = 1;
                    String n = "قام " + User.CurrentUser.userName + "بحذف عدد " + rec.Num + " " + rec.DName + " " + " السبب :" + ConfirmReasonAlertController.delReson;
                    DatabaseHandler.insertIntoRoomsNote("Cafe", n, roomName.getText());
                    fillDrinkTable();
                    updateTotalPRice();
                    UpdateFinalPriceLabel();
                    DatabaseHandler.stat.close();
                }
            } catch (Exception e) {
                Logger.writeLog("Exception in " + getClass().getName() + ".DeleteDrinkRecord() : " + e);
            }
        };
    }

    @FXML
    private void fillDrinksNames() {
        Drinks.setItems(DatabaseHandler.selectDrinksPriceDeviceNames("WHERE category='" + proCategCombo.getValue() + "'"));
        drinksSearchCombo.setItems(Drinks.getItems());
    }

    public void UpdateFinalPriceLabel() {
        double dis = 0;
        double prevPrice = Double.parseDouble(totalPRiceLabel.getText().replaceAll(ControlPanel.getInstance().getCurrency(), ""));
        try {
            dis = Double.parseDouble(DisCountTextF.getText());
        } catch (NumberFormatException e) {
        }
        prevPrice = HelperMethods.formatNum(prevPrice - dis);
        if (prevPrice < 0 || dis < 0) {
            finalPrice.setText(totalPRiceLabel.getText());

        } else {
            finalPrice.setText(prevPrice + ControlPanel.getInstance().getCurrency());
        }

    }

    @FXML
    private void leaveRoomAction() {
        leaveRoom(false);
    }

    public void leaveRoom(boolean printOption) {
        try {
            if (MAIN_TIMELINE.getStatus().equals(Animation.Status.PAUSED)
                    || MAIN_TIMELINE.getStatus().equals(Animation.Status.RUNNING)) {
                endTimer();
            }
            float price = Float.parseFloat(finalPrice.getText().replaceAll(ControlPanel.getInstance().getCurrency(), ""));
            float prePrice = Float.parseFloat(totalPRiceLabel.getText().replaceAll(ControlPanel.getInstance().getCurrency(), ""));
            float playingIncome = 0;
            if (PLAYING_DATA.isEmpty() && !ControlPanel.getInstance().DRINKS_WITHOUT_TIMER) {
                return;
            }
            String PlayingData = "";
            String DrinkData = "";
            LocalTime globalFromTime = !PLAYING_DATA.isEmpty() ? LocalTime.parse(PLAYING_DATA.get(0).getFromTime(), DATE_TIME_FORMAT_2) : LocalTime.now();
            LocalTime globalToTime = !PLAYING_DATA.isEmpty() ? LocalTime.parse(PLAYING_DATA.get(0).getToTime(), DATE_TIME_FORMAT_2) : LocalTime.now();
            DatabaseHandler.insertDataIntoDailyStorePullData(DRINK_DATA);
            for (int i = 0; i < PLAYING_DATA.size(); i++) {
                if (LocalTime.parse(PLAYING_DATA.get(i).getFromTime(), DATE_TIME_FORMAT_2).isBefore(globalFromTime)) {
                    globalFromTime = LocalTime.parse(PLAYING_DATA.get(i).getFromTime(), DATE_TIME_FORMAT_2);
                }
                if (LocalTime.parse(PLAYING_DATA.get(i).getToTime(), DATE_TIME_FORMAT_2).isAfter(globalToTime)) {
                    globalToTime = LocalTime.parse(PLAYING_DATA.get(i).getToTime(), DATE_TIME_FORMAT_2);
                }
                if (!(PLAYING_DATA.get(i).getPlayingPrice().equals("0.0"))) {
                    PlayingData += PLAYING_DATA.get(i).getDeviceName() + "@"
                            + PLAYING_DATA.get(i).getFromTime() + "@"
                            + PLAYING_DATA.get(i).getToTime() + "@"
                            + PLAYING_DATA.get(i).getPlayingStatus() + "@"
                            + PLAYING_DATA.get(i).getPlayingPrice() + "#";
                }
                playingIncome += Float.parseFloat(PLAYING_DATA.get(i).getPlayingPrice().trim());

            }
            for (int i = 0; i < DRINK_DATA.size(); i++) {
                DrinkData += DRINK_DATA.get(i).id + "@"
                        + DRINK_DATA.get(i).Num + "@"
                        + DRINK_DATA.get(i).DName + "@"
                        + DRINK_DATA.get(i).Tprice + "@"
                        + DRINK_DATA.get(i).categ
                        + "#";
            }
            String date = Methods.getMyDay();
            String UserName = User.CurrentUser.userName;
            String roomNum = roomName.getText();
            float Dis = 0.0f;
            try {
                Dis = Float.parseFloat(DisCountTextF.getText());
                if (Dis < 0 || Dis > prePrice) {
                    Dis = 0.0f;
                }
            } catch (Exception e) {
            }

            ArrayList<String> cafeNotesList = DatabaseHandler.getMyNotesForsection("Cafe", roomName.getText());
            String cafeNotesStr = "";
            for (int i = 0; i < cafeNotesList.size(); i++) {
                cafeNotesStr += cafeNotesList.get(i) + "#";
            }

            PlayingRecord rec;
            List<playingRecordForPrint> forPrint = new ArrayList<>();
            int timeInMinutes = 0;
            for (int i = 0; i < PLAYING_DATA.size(); i++) {
                rec = PLAYING_DATA.get(i);
                timeInMinutes += getDifferranceInMinutes(LocalTime.parse(rec.getFromTime(), DATE_TIME_FORMAT_2), LocalTime.parse(rec.getToTime(), DATE_TIME_FORMAT_2));
                forPrint.add(new playingRecordForPrint(rec.getDeviceName(), rec.getFromTime(), rec.getToTime(), rec.getPlayingStatus(), rec.getPlayingPrice(), 0, rec.getRoomName()));
            }

//            String Builder to Save all pormotion Settings 
            StringBuilder sp = new StringBuilder();

//          ==== Add Custoemr Time And Check For Promotions ====
            String cutomerName = setCustomerName();
            if (!cutomerName.equals("")) {
                DatabaseHandler.con.prepareStatement("UPDATE customers SET chargeMins=chargeMins+" + timeInMinutes + " , totalMins=totalMins+" + timeInMinutes + " where name='" + cutomerName + "'").execute();
                if (ControlPanel.getInstance().HOUR_PROMOTIONS) {
                    Dis = checkForPormotions(cutomerName, Dis, playingIncome, prePrice, sp);
                    price = prePrice - Dis;
                    Customer.fillData();
                }
            }
//            ==== Device Pormotions ====
            float devicePormotionDiscount = 0;
            if (ControlPanel.getInstance().DEVICE_PROMOTIONS && PlayingPriceController.IS_DISCOUNT_ACTIVE.get()) {
                Map<String, Boolean> map = DatabaseHandler.selectDeviceNamesWhichApplayPormotion();
                int DevicePormotionsPercentage = Integer.parseInt(PlayingPriceController.devicePercentage.get().replaceAll("%", "").trim());
                for (PlayingRecord pRec : PLAYING_DATA) {
                    if (map.get(pRec.getDeviceName()) != null) {
                        devicePormotionDiscount += Double.parseDouble(pRec.getPlayingPrice()) * (DevicePormotionsPercentage / 100.00);
                    }
                }
            }
            if (devicePormotionDiscount > 0) {
                String msg = String.format("تم خصم %s %s عروض أجهزة", String.valueOf(devicePormotionDiscount), ControlPanel.getInstance().getCurrency());
                sp.append(msg).append("\n");
                DatabaseHandler.insertIntoRoomsNote("play", msg, roomName.getText());
                Dis = (Dis + devicePormotionDiscount) > prePrice ? prePrice : (Dis + devicePormotionDiscount);
                price = prePrice - Dis;

            }

//          ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== ==== 
            ArrayList<String> playNotesList = DatabaseHandler.getMyNotesForsection("play", roomName.getText());
            String playNotesStr = "";
            for (int i = 0; i < playNotesList.size(); i++) {
                playNotesStr += playNotesList.get(i) + "#";
            }

            if (!sp.toString().trim().equalsIgnoreCase("")) {
                DialogHelper.getInstance().showOKAlert(sp.toString(), false);
            } else {
            }

            int printerFlag = 0;
            int ReciptId = DatabaseHandler.getReceetID();
            if (prePrice > 0) {
                DatabaseHandler.insertInDailySheet(new DailySheetRecord(
                        ReciptId,
                        date,
                        UserName,
                        roomNum,
                        globalFromTime.format(DATE_TIME_FORMAT_2),
                        globalToTime.format(DATE_TIME_FORMAT_2),
                        prePrice,
                        Order.getTax("rooms"),
                        Order.getServant("rooms"),
                        Dis,
                        price,
                        PlayingData,
                        DrinkData, playNotesStr, cafeNotesStr, ""
                ), "dailySheet", cutomerName.equals("") ? null : cutomerName);
                printerFlag = 1;
            }

            TempMethods.ClearData("roomsnotes WHERE RoomNum='" + roomName.getText() + "'");
            String sql = "DELETE FROM " + PLAY_TABLENAME + " where roomName=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, roomName.getText());
            DatabaseHandler.stat.execute();
            sql = "DELETE FROM " + DRINKS_TABLENAME + " where roomName=?";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sql);
            DatabaseHandler.stat.setString(1, roomName.getText());
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
            DatabaseHandler.con.prepareStatement("DELETE FROM tempRoomsCutomers WHERE roomName='" + roomName.getText() + "'").execute();
            fillDrinkTable();
            fillPlayingTable();
            updateTotalPRice();
            UpdateFinalPriceLabel();
            resetPlayingInputs();
            discountPercentage.setText("");
            DisCountTextF.setText("");
            leaveRoomNotation = 1;
            roomBox.setAvailable();
            closeWindow();
            if (printerFlag == 1) {
                ReceetClass dataForPrint = new ReceetClass(ReciptId, date, UserName, roomNum, globalFromTime.format(DATE_TIME_FORMAT_2), globalToTime.format(DATE_TIME_FORMAT_2), prePrice, Order.getTax("rooms"), Order.getServant("rooms"), Dis, price, forPrint, DrinkData);
                new Thread(() -> {
                    DatabaseHandler.ShowReceet(dataForPrint, roomName.getText(), printOption);
                }).start();
            }
            if (Preferences.getInstance().getBoolean(PreferencesType.OpenCashDrawerWithSale, "true")) {
                CashDrawer.openCashDrawer();
            }
        } catch (Exception e) {
            Logger.writeLog("Exception  In RoomController ->leaveRoomAction:-" + e);
        }
    }

    public void EndCurrntTimer() {
        try {
            String sqlString = "update " + PLAY_TABLENAME + " set status=0 where roomName=? And status=1";
            DatabaseHandler.stat = DatabaseHandler.con.prepareStatement(sqlString);
            DatabaseHandler.stat.setString(1, roomName.getText());
            DatabaseHandler.stat.execute();
            DatabaseHandler.stat.close();
        } catch (Exception e) {
            Logger.writeLog("Exception  In RoomController" + (roomName.getText()) + " -> EndCurrntTimer:-" + e);
        }
    }

    private void keyAct(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow();
        }
    }

    @FXML
    private void escact(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow();
        }
    }

    @FXML
    private void fillCategNames() {

        proCategCombo.setItems(DatabaseHandler.selectCategotiesNames(""));
        productCategSearchCombo.setItems(proCategCombo.getItems());
        Drinks.setValue(null);
    }

    @FXML
    private void transferMethod() {
        try {
            if (!PLAYING_DATA.isEmpty() || !DRINK_DATA.isEmpty()) {
                TransferController.transferClick = 0;
                TransferController.AVRoomsComboBoxPointer.setValue(null);
                TransferController.fromTextField_Static.setText(roomBox.getRoomNumber());
//                TransferController.sourceRoom = roomName.getText();
                LoadHelper.getInstance().stage_transfer.showAndWait();

                if (TransferController.transferClick == 1) {
                    if (MAIN_TIMELINE.getStatus().equals(Animation.Status.PAUSED)
                            || MAIN_TIMELINE.getStatus().equals(Animation.Status.RUNNING)) {
                        endTimer();
                    }
                    String sql;
                    String note = "قام " + User.CurrentUser.userName + " " + " بتحويل حساب " + roomName.getText() + " " + "الي " + " " + sourceRoom + " ";
                    DatabaseHandler.insertIntoRoomsNote("play", note, roomName.getText());
                    sql = "UPDATE room1playing SET roomName='" + sourceRoom + "' WHERE roomName='" + roomName.getText() + "'";
                    DatabaseHandler.con.prepareStatement(sql).execute();
                    sql = "UPDATE room1drink SET roomName='" + sourceRoom + "' WHERE roomName='" + roomName.getText() + "'";
                    DatabaseHandler.con.prepareStatement(sql).execute();
                    sql = "UPDATE roomsnotes SET RoomNum='" + sourceRoom + "' WHERE RoomNum='" + roomName.getText() + "'";
                    DatabaseHandler.con.prepareStatement(sql).execute();
                    sql = "UPDATE tempRoomsCutomers SET roomName='" + sourceRoom + "' WHERE RoomName='" + roomName.getText() + "'";
                    DatabaseHandler.con.prepareStatement(sql).execute();
                    new TransferController().updateRoom(sourceRoom, roomName.getText());
                    fillPlayingTable();
                    fillDrinkTable();
                    updateTotalPRice();
                    UpdateFinalPriceLabel();
                    roomBox.setAvailable();
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> transferMethod :- " + e);
        }
    }

    @FXML
    private void barAction() {
        if (detailsBox.isVisible()) {
            detailsBox.setVisible(false);
        } else {
            detailsBox.setVisible(true);
        }
    }

    @FXML
    private void chooseClientAction() {
        try {
            Customer chosenCustomer = DialogHelper.getInstance().chooseCustomer();
            if (chosenCustomer != null) {
                addRoomCutomer(roomName.getText(), chosenCustomer.getFullName());
                customerNameTextField.setText(chosenCustomer.getFullName());
            } else {
                DatabaseHandler.con.prepareStatement("DELETE FROM tempRoomsCutomers WHERE roomName='" + roomName.getText() + "'").execute();
                customerNameTextField.setText("");
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> chooseClientAction() :- " + e);
        }
    }

    private void addRoomCutomer(String roomName, String customername) {
        try {
            DBCRUD obj = new DBCRUD("tempRoomsCutomers") {
                @Override
                public ArrayList createMap() {
                    ArrayList data = new ArrayList();
                    data.add(new DBField(roomName, "roomName", "PK"));
                    data.add(new DBField(customername, "customerName", "NN"));
                    return data;
                }
            };
            obj.setFields(obj.createMap());
            obj.add();
            obj.update();
        } catch (Exception e) {
            Logger.writeLog("Exception in " + getClass().getName() + " -> addRoomCutomer :- " + e);
        }
    }

    public String setCustomerName() {
        try {
            if (roomBox.isAvailable()) {
                DatabaseHandler.con.prepareStatement("DELETE FROM tempRoomsCutomers WHERE roomName='" + roomName.getText() + "'").execute();
                customerNameTextField.setText("");

            } else {
                ResultSet res = DatabaseHandler.con.prepareStatement("SELECT customerName FROM tempRoomsCutomers WHERE roomName='" + roomName.getText() + "'").executeQuery();
                while (res.next()) {
                    customerNameTextField.setText(res.getString(1));
                    return res.getString(1);
                }
            }
        } catch (Exception e) {
            Logger.writeLog("Exception in Room1Controller -> setCustomerName :- " + e);
        }
        return "";
    }

    public int getDifferranceInMinutes(LocalTime f, LocalTime t) {
        LocalTime diff = t.minusHours(f.getHour()).minusMinutes(f.getMinute());
        return diff.getMinute() + (diff.getHour() * 60);
    }

    private float checkForPormotions(String cutomerName, float userInputDiscount, float totalPlayingIncome, float totalIcomne, StringBuilder sp) {
        try {
            Promotion currentPromotion = getTheCurrentPromotion();
            if (currentPromotion.getDiscountPercentage() > 0 && currentPromotion.getNumOfHours() > 0) {
                ResultSet res = DatabaseHandler.con.prepareStatement("SELECT chargeMins/60 FROM customers WHERE name='" + cutomerName + "'").executeQuery();
                double hrs = 0;
                while (res.next()) {
                    hrs = HelperMethods.formatNum(res.getDouble(1));
                }
                if (hrs > 0 && hrs >= currentPromotion.getNumOfHours()) {
                    double dicountVal = HelperMethods.formatNum(totalPlayingIncome * (currentPromotion.getDiscountPercentage() / 100));
                    userInputDiscount += dicountVal;
                    userInputDiscount = userInputDiscount > totalIcomne ? totalIcomne : userInputDiscount;
                    String userMsg = "تم خصم " + dicountVal + " " + controlpanel.ControlPanel.getInstance().getCurrency() + " عروض ساعات";
                    sp.append(userMsg).append("\n");
                    DatabaseHandler.insertIntoRoomsNote("play", userMsg, roomName.getText());
                    // ((hrs - currentPromotion.getNumOfHours())*60)
                    DatabaseHandler.con.prepareStatement("UPDATE customers SET chargeMins=0 WHERE name='" + cutomerName + "'").execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.writeLog("Exception in Room1Controller -> setCustomerName :- " + e);
        }
        return userInputDiscount;
    }

    public void setDataAndStart(String deviceName, String type, String customerName, boolean openSelect) {
        DeviceKind.setValue(deviceName);
        singleRadioButton.setSelected(type.trim().equalsIgnoreCase("single"));
        MultiRadioButton.setSelected(type.trim().equalsIgnoreCase("Multi"));
        customerNameTextField.setText(String.valueOf(customerName));
        addRoomCutomer(roomName.getText(), customerName);
        kindOfTime.setSelected(openSelect);
        startTimer();
//        startTimerButton.fire();
    }

    public void SetRoomBox(RoomBox roomBox) {
        this.roomBox = roomBox;
//        EndCurrntTimer();
        fillPlayingTable();
        fillDrinkTable();

    }

    @FXML
    private void discountTextAction() {
        try {
            double price = Double.parseDouble(totalPRiceLabel.getText().replaceAll(ControlPanel.getInstance().getCurrency(), ""));
            if (price == 0) {
                discountPercentage.setText("");
                DisCountTextF.setText("");
                return;
            } else if (DisCountTextF.getText().trim().equals("")) {
                discountPercentage.setText("");
            }
            double discountValue = Double.parseDouble(DisCountTextF.getText());
            discountPercentage.setText(String.valueOf(HelperMethods.formatNum((discountValue / price) * 100)));
            UpdateFinalPriceLabel();
        } catch (NumberFormatException e) {
            finalPrice.setText(totalPRiceLabel.getText());
        }
    }

    @FXML
    private void discountPercentageHandle() {
        try {
            double price = Double.parseDouble(totalPRiceLabel.getText().replaceAll(ControlPanel.getInstance().getCurrency(), ""));
            if (price == 0) {
                discountPercentage.setText("");
                DisCountTextF.setText("");
                return;
            } else if (discountPercentage.getText().trim().equals("")) {
                DisCountTextF.setText("");
            }
            double percentage = Double.parseDouble(discountPercentage.getText());
            DisCountTextF.setText(String.valueOf(HelperMethods.formatNum((percentage / 100) * price)));
            UpdateFinalPriceLabel();
        } catch (NumberFormatException e) {
            finalPrice.setText(totalPRiceLabel.getText());
        }
    }

    public void resumeCurrentTime() {
        PlayingRecord lastRecord = selectActivePlayingRecordAndRemove();
        if (lastRecord != null) {
            isResume = true;
            StartTime = LocalTime.parse(lastRecord.getFromTime(), DATE_TIME_FORMAT_2);
            singleRadioButton.setSelected(lastRecord.getPlayingStatus().equalsIgnoreCase("Single"));
            MultiRadioButton.setSelected(lastRecord.getPlayingStatus().equalsIgnoreCase("Multi"));
            if (lastRecord.getFinishTime().trim().equals("")) {
                // ... Open Time Case ...
                kindOfTime.setSelected(true);
                LocalTime playingTime = EditPlayingRecordController.getDifferrance(LocalTime.parse(lastRecord.getFromTime(), DATE_TIME_FORMAT_2), LocalTime.now());
                time = LocalTime.parse("00:00:00").plusHours(playingTime.getHour()).plusMinutes(playingTime.getMinute());
                setDataAndStart(lastRecord.getDeviceName(), lastRecord.getPlayingStatus(), setCustomerName(), true);
            } else {
                // ... Finite Time Case ... 
                LocalTime duration = LocalTime.parse(lastRecord.getFinishTime(), DATE_TIME_FORMAT_1);
                LocalTime finishTime = StartTime.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
                boolean isPassed = LocalTime.now().isAfter(finishTime);
                System.out.println("Passed");

                if (isPassed) {
                    DatabaseHandler.insertInPlayingTable(new PlayingRecord(
                            0,
                            lastRecord.getDeviceName(),
                            lastRecord.getFromTime(),
                            finishTime.format(DATE_TIME_FORMAT_2),
                            lastRecord.getPlayingStatus(),
                            DatabaseHandler.getPrice(duration.getHour(), duration.getMinute(), lastRecord.getPlayingStatus(), lastRecord.getDeviceName()),
                            0,
                            roomName.getText(),
                            ""));
                    fillPlayingTable();
                    updateTotalPRice();
                    UpdateFinalPriceLabel();
                    resetPlayingInputs();
                } else {
                    kindOfTime.setSelected(false);
                    hours.getValueFactory().setValue(duration.getHour());
                    mins.getValueFactory().setValue(duration.getMinute());
                    LocalTime playingTime = EditPlayingRecordController.getDifferrance(LocalTime.parse(lastRecord.getFromTime(), DATE_TIME_FORMAT_2), LocalTime.now());
                    time = LocalTime.parse("00:00:00").plusHours(playingTime.getHour()).plusMinutes(playingTime.getMinute());
                    setDataAndStart(lastRecord.getDeviceName(), lastRecord.getPlayingStatus(), setCustomerName(), false);
                }
            }
        }
    }

    @FXML
    private void leaveAndPrint() {
        System.out.println("leaveAndPrint() ...");
        leaveRoom(true);
    }

    @FXML
    private void barCodeToggle() {
        if (chooseFromBarCode_Box.isVisible()) {
            // hide barcode
            chooseFromBarCode_Box.setVisible(false);
            // show list box
            chooseFromList_Box.setVisible(true);
            barcodeIcon.setIcon(MaterialDesignIcon.BARCODE_SCAN);
        } else {
            // hide list box
            chooseFromList_Box.setVisible(false);
            // show barcode
            barocdeTextField.setText("");
            barocdeTextField.requestFocus();
            chooseFromBarCode_Box.setVisible(true);
            barcodeIcon.setIcon(MaterialDesignIcon.SORT_NUMERIC);
        }
    }

    @FXML
    private void barcodeAction() {
        System.out.println("Barcode Enter Action..");
        System.out.println("Value: " + barocdeTextField.getText());
        String[] data = getProductByBarCode(barocdeTextField.getText().trim());
        if (data != null) {
            boolean result = buyDrink(data[0], data[1], 1);
            if (result) {
                barocdeTextField.setText("");
            }
        } else {
            DialogHelper.getInstance().showOKAlert("الكود غير موجود!");
        }

    }

}
