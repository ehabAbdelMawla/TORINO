package dialog.edit.playing.room.playing;

import datamodel.PlayingRecord;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTimePicker;
import datamodel.User;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import util.Logger;
import util.db.DatabaseHandler;
import util.gui.load.DialogHelper;

public class EditPlayingRecordController implements Initializable {

    public static int flag;
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    public static PlayingRecord previousRecord;
    public static String note = "";

    // === GUI Objects ===
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private JFXComboBox<String> DeviceKind;
    @FXML
    private JFXTimePicker f;
    @FXML
    private JFXTimePicker t;
    @FXML
    private JFXRadioButton s;
    @FXML
    private JFXRadioButton m;
    @FXML
    private ToggleGroup playStat;
    @FXML
    private TextField reason;
    @FXML
    private Label error;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        flag = 0;
        DeviceKind.setItems(DatabaseHandler.selectPlayingPriceDeviceNames());
        setStartedData();
        error.setVisible(false);
    }

    @FXML
    private void closeWindow(Event event) {
        error.setVisible(false);
        ((Stage) ((Node) (event.getSource())).getScene().getWindow()).close();
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
    private void editAction(Event event) {
        if ("".equals(reason.getText().trim())) {
            error.setVisible(true);
            return;
        }
        try {
            if (f.getValue() == null || t.getValue() == null) {
                DialogHelper.getInstance().showOKAlert("- يجب تحديد وقت البداية و النهاية .");
                return;
            }
            error.setVisible(false);
            String kind = "Single";
            if (m.isSelected()) {
                kind = "Multi";
            }
            LocalTime diff = getDifferrance(f.getValue(), t.getValue());
            String price = DatabaseHandler.getPrice(diff.getHour(), diff.getMinute(), kind, DeviceKind.getValue());
            String cuTime = LocalTime.now().format(DATE_TIME_FORMATTER);
            String reasonStr = reason.getText().trim();
            if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل نوع الللعب من  " + " " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل وقت البداية للجهاز  " + " " + previousRecord.getDeviceName() + " من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل وقت النهاية للجهاز  " + " " + previousRecord.getDeviceName() + " من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "وتعديل  نوع اللعب من " + " " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + User.CurrentUser.userName + " " + "بتعديل نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "وتعديل   وقت البداية من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "وتعديل  وقت النهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل نوع اللعب للجهاز  " + " " + previousRecord.getDeviceName() + " " + " من " + " " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + " " + "وتعديل  وقت البداية من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل نوع اللعب للجهاز  " + " " + previousRecord.getDeviceName() + " " + " من " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + " " + "وتعديل  وقت النهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل وقت البداية للجهاز  " + " " + previousRecord.getDeviceName() + " " + " من " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + "وتعديل  وقت النهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل  نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "وتعديل  وقت البداية من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + " وتعدبل نوع اللعب  من " + " " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + " " + " السبب : " + " " + reasonStr + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل  نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "وتعديل  وقت البداية من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + "وتعديل وقت النهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل  نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "وتعديل نوع اللعب من " + " " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + " " + "وتعديل  وقت التهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + "بتعديل نوع اللعب للجهاز " + " " + previousRecord.getDeviceName() + " من " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + "" + "وتعديل  وقت البداية من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + " وتعدبل وقت النهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            } else if (!DeviceKind.getValue().equals(previousRecord.getDeviceName()) && !previousRecord.getPlayingStatus().equalsIgnoreCase(kind) && !previousRecord.getFromTime().equalsIgnoreCase(f.getValue().format(DATE_TIME_FORMATTER)) && !previousRecord.getToTime().equalsIgnoreCase(t.getValue().format(DATE_TIME_FORMATTER))) {
                note = " قام" + " " + User.CurrentUser.userName + " " + "بتعديل  نوع الجهاز من  " + " " + previousRecord.getDeviceName() + " " + "الي " + " " + DeviceKind.getValue() + " " + "و تعديل نوع اللعب من " + " " + previousRecord.getPlayingStatus() + " " + "الي " + " " + kind + "" + "وتعديل  وقت البداية من " + " " + previousRecord.getFromTime() + " " + "الي " + " " + f.getValue().format(DATE_TIME_FORMATTER) + " " + " وتعدبل وقت النهاية من " + " " + previousRecord.getToTime() + " " + "الي " + " " + t.getValue().format(DATE_TIME_FORMATTER) + " " + " السبب : " + " " + reasonStr + " " + "وقت التعديل : " + " " + cuTime + " ";
            }
            previousRecord = new PlayingRecord(previousRecord.getId(),
                    DeviceKind.getValue(),
                    f.getValue().format(DATE_TIME_FORMATTER),
                    t.getValue().format(DATE_TIME_FORMATTER),
                    kind,
                    price, 0, previousRecord.getRoomName(), previousRecord.getFinishTime());
            flag = 1;
            closeWindow(event);
        } catch (Exception e) {
            Logger.writeLog("Exeption in " + getClass().getName() + " -> editAction:-" + e);
        }
    }

    public static LocalTime getDifferrance(LocalTime f, LocalTime t) {
        LocalTime diff = t;
        diff = diff.minusHours(f.getHour()).minusMinutes(f.getMinute());
        return diff;
    }

    public void setStartedData() {
        LocalTime from = LocalTime.parse(previousRecord.getFromTime(), DATE_TIME_FORMATTER);
        LocalTime to = LocalTime.parse(previousRecord.getToTime(), DATE_TIME_FORMATTER);
        f.setValue(from);
        t.setValue(to);
        DeviceKind.setValue(previousRecord.getDeviceName());
        if (previousRecord.getPlayingStatus().equalsIgnoreCase("Multi")) {
            m.setSelected(true);
        } else {
            s.setSelected(true);
        }
    }

    @FXML
    private void ketAct(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            editAction((Event) event);
        } else if (event.getCode() == KeyCode.ESCAPE) {
            closeWindow((Event) event);
        }
    }
}
